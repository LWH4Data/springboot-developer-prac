package me.shinsunyoung.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.config.jwt.TokenProvider;
import me.shinsunyoung.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.shinsunyoung.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import me.shinsunyoung.springbootdeveloper.repository.RefreshTokenRepository;
import me.shinsunyoung.springbootdeveloper.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.security.autoconfigure.web.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

  private final OAuth2UserCustomService oAuth2UserCustomService;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserService userService;

  @Bean
  // 스프링 시큐리티 기능 비활성화
  public WebSecurityCustomizer configure() {
    return (web) -> web.ignoring()
            .requestMatchers(toH2Console())
            .requestMatchers(
                    "/img/**",
                    "/css/**",
                    "/js/**"
            );
  }

  // 토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼 로그인, 세션 비활성화.
  @Bean
  // filterChain(): 토큰 방식으로 인증을 하기 때문에 기존 폼 로그인, 세션 기능을 비활성화한다.
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(management -> management.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))
            // 헤더 확인을 위한 커스텀 필터 추가.
            //   - addFilterBefore: 9장에서 구현한 커스텀 필터 TokenAuthenticationFilter 클래스이다.
            .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            // 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요.
            .authorizeHttpRequests(auth -> auth
                    // 토큰 재발급 URL은 인정 없이 접근하도록 설정.
                    .requestMatchers("/api/token").permitAll()
                    // 로그아웃(리프레시 토큰 삭제)도 액세스 토큰 만료 상태에서 호출 가능해야 한다.
                    .requestMatchers("/api/refresh-token").permitAll()
                    // 나머지 API는 모두 인증을 해야 접근하도록 설정.
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll())
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    // Authorization 요청과 관련된 상태 저장.
                    //   - OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있도록
                    //     인증 요청과 관련된 상태를 저장할 저장소를 설정한다.
                    .authorizationEndpoint(authorizationEndpoint ->
                            authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                    .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                    // 인증 성공 시 실행할 핸들러.
                    .successHandler(oAuth2SuccessHandler())
            )
            // "/api"로 시작하는 url인 경우 401 사태 코드를 반환하도록 예외 처리.
            .exceptionHandling(ex -> ex
                    .defaultAuthenticationEntryPointFor(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                            // "/api"로 시작하는 쵸엉인 경우 실패하면 401 반환.
                            request -> request.getRequestURI().startsWith("/api/")
                    )
            )
            .build();
  }

  @Bean
  public OAuth2SuccessHandler oAuth2SuccessHandler() {
    return new OAuth2SuccessHandler(tokenProvider,
            refreshTokenRepository,
            oAuth2AuthorizationRequestBasedOnCookieRepository(),
            userService
    );
  }

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter(tokenProvider);
  }

  @Bean
  public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
    return new OAuth2AuthorizationRequestBasedOnCookieRepository();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
