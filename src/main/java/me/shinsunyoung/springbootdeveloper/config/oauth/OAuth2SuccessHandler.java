package me.shinsunyoung.springbootdeveloper.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.config.jwt.TokenProvider;
import me.shinsunyoung.springbootdeveloper.domain.RefreshToken;
import me.shinsunyoung.springbootdeveloper.domain.User;
import me.shinsunyoung.springbootdeveloper.repository.RefreshTokenRepository;
import me.shinsunyoung.springbootdeveloper.service.UserService;
import me.shinsunyoung.springbootdeveloper.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
  public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
  public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
  public static final String REDIRECT_PATH = "/articles";

  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
  private final UserService userService;

  // 토큰과 관련된 작업만 추가로 처리하기 위해 SimpleUrlAuthenticationSuccessHandler를 상속받아
  // onAuthenticationSuccess() 메서드를 오버라이드 한다.
  //   - 스프링 시큐리티 기본 로직에서는 별도의 authenticationSuccessHandler를 지정하지 않으면
  //     로그인 성공 후 SimpleUrlAuthenticationSuccessHandler를 사용한다.
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

    // 리프래시 토큰 생성 → 저장[saveRefreshToken()] → 쿠기에 저장[addRefreshTokenToCookie()].
    String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
    saveRefreshToken(user.getId(), refreshToken);
    addRefreshTokenToCookie(request, response, refreshToken);

    // 액세스 토큰 생성 → 패스에 액세스 토큰 추가.
    //   - 액세스 토큰 클라이언트에게 전달 예. http://localhost:8080/article?token=eyJ0eXAiOiJK...
    String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
    String targetUrl = getTargetUrl(accessToken);

    // 인증 관련 설정값, 쿠키 제거.
    //   - 인증 프로세스를 진행하며 세션과 쿠키에 임시로 저장된 인증 관련 데이터를 제거한다.
    clearAuthenticationAttributes(request, response);

    // 리다이렉트.
    //   - targetUrl로 리다이렉트한다.
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  // 생성된 리프레시 토큰을 전달받아 데이터베이스에 저장.
  private void saveRefreshToken(Long userId, String newRefreshToken) {
    RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
            .map(entity -> entity.update(newRefreshToken))
            .orElse(new RefreshToken(userId, newRefreshToken));

    refreshTokenRepository.save(refreshToken);
  }

  // 생성된 리프레시 토큰을 쿠키에 저장.
  private void addRefreshTokenToCookie(HttpServletRequest request,
                                       HttpServletResponse response, String refreshToken) {

    // refresh token 쿠키의 만료 시간을 초 단위로 변환한다.
    int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

    // 기존 refresh token 쿠키가 남아 있을 수 있으므로 먼저 삭제한다.
    // 같은 이름의 쿠키라도 path/domain이 다르면 중복될 수 있기 때문에 안전하게 초기화한다.
    CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);

    // 새로 발급한 refresh token을 쿠키에 저장한다.
    CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
  }

  // 인증 관련 설정값, 쿠키 제거.
  private void clearAuthenticationAttributes(HttpServletRequest request,
                                             HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  // 액세스 토큰을 패스에 추가.
  private String getTargetUrl(String token) {
    return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
            .queryParam("token", token)
            .build()
            .toUriString();
  }
}
