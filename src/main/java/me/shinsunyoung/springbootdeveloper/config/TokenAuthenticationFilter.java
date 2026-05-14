package me.shinsunyoung.springbootdeveloper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.config.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private final TokenProvider tokenProvider;
  private final static String HEADER_AUTHORIZATION = "Authorization";
  private final static String TOKEN_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain
  ) throws ServletException, IOException {

    // 1. 클라이언트가 보낸 요청 헤더에서 Authorization 값을 꺼낸다.
    // 예: Authorization: Bearer eyJhbGciOiJIUzI1...
    String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

    // 2. "Bearer " 부분을 제거하고 순수 JWT 토큰 문자열만 꺼낸다.
    String token = getAccessToken(authorizationHeader);

    // 3. 토큰이 위조되지 않았고, 만료되지 않았는지 검증한다.
    if (tokenProvider.validToken(token)) {

      // 4. 토큰 안의 사용자 정보를 바탕으로
      // Spring Security가 이해할 수 있는 Authentication 객체를 만든다.
      Authentication authentication = tokenProvider.getAuthentication(token);

      // 5. 현재 요청은 이 사용자로 인증되었다고 Spring Security에 저장한다.
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 6. 이 필터의 일이 끝났으니 다음 필터 또는 컨트롤러로 요청을 넘긴다.
    filterChain.doFilter(request, response);
  }

  private String getAccessToken(String authorizationHeader) {
    // null이 아니거나 Bearer 헤더로 시작하는 경우 Header의 길이를 반환.
    if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
      return authorizationHeader.substring(TOKEN_PREFIX.length());
    }
    // 그 외에는 null을 반환.
    return null;
  }
}
