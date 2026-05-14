//package me.shinsunyoung.springbootdeveloper.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.boot.security.autoconfigure.web.servlet.PathRequest.toH2Console;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class WebSecurityConfig {
//
//  // 특정 요청 경로를 Spring Security 필터 체인에서 제외하는 설정
//  @Bean
//  public WebSecurityCustomizer configure() {
//    //  스프링 시큐리티의 모든 기능을 사용하지는 않게 하는 설정 코드이다.
//    //  - 모든 서비스에서 인증, 인가를 사용하지는 않는다.
//    return (web) -> web.ignoring()
//            // H2 콘솔 경로는 Spring Security 적용 대상에서 제외
//            .requestMatchers(toH2Console())
//            // 정적 리소스 경로는 Spring Security 적용 대상에서 제외
//            .requestMatchers("/static/**");
//  }
//
//  // 특정 HTTP 오청에 대한 웹 기반 보안 구성.
//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    return http
//            // 인증, 인가 설정.
//            .authorizeHttpRequests(auth -> auth
//                    // requestMatchers(): 특정 요청과 일치하는 url에 대한 액세스를 설정한다.
//                    .requestMatchers(
//                            "/login",
//                            "/signup",
//                            "/user"
//                    // permitAll(): 누구나 접근이 가능하게 설정한다.
//                    // 즉, 위에서 세팅한 "/login", "/signup", "/user"에 요청이 오면 누구나 접근이 가능하다.
//                    ).permitAll()
//                    // anyRequest(): 위에서 설정한 url 이외의 요총에 대해 설정한다.
//                    // authenticated(): 별도의 인가는 필요하지 않지만 인증이 성공된 상태여야 접근할 수 있다.
//                    .anyRequest().authenticated())
//            // 폼 기반 로그인 설정.
//            .formLogin(formLogin -> formLogin
//                    // loginPage(): 로그인 페이지 경로를 설정한다.
//                    .loginPage("/login")
//                    // defaultSuccessUrl(): 로그인이 완료되었을 때 이동할 경로를 설정한다.
//                    .defaultSuccessUrl("/articles")
//            )
//            // 로그아웃 설정.
//            .logout(logout -> logout
//                    // logoutSeuccessUrl(): 로그아웃이 완료되었을 때 이동할 경로를 설정한다.
//                    .logoutSuccessUrl("/login")
//                    // invalidateHttpSession(): 로그아웃 이후에 세션을 전체 삭제할지 여부를 설정한다.
//                    .invalidateHttpSession(true)
//            )
//            // csrf(): CSRF 공격을 방지할지 여부를 결정한다. (실습이라 비활성화).
//            .csrf(AbstractHttpConfigurer::disable)
//            .build();
//    }
//  // 패스워드 인코더로 사용할 빈 등록.
//  @Bean
//  public PasswordEncoder passwordEncoder() {
//    return new BCryptPasswordEncoder();
//  }
//}
