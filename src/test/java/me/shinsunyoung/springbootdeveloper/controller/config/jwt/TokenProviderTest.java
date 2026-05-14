package me.shinsunyoung.springbootdeveloper.controller.config.jwt;

import io.jsonwebtoken.Jwts;
import me.shinsunyoung.springbootdeveloper.config.jwt.JwtProperties;
import me.shinsunyoung.springbootdeveloper.config.jwt.TokenProvider;
import me.shinsunyoung.springbootdeveloper.domain.User;
import me.shinsunyoung.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TokenProviderTest {
  @Autowired
  private TokenProvider tokenProvider;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private JwtProperties jwtProperties;

  // generateToken() 검증 테스트.
  @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
  @Test
  void generateToken() {
    // given
    //   - 토큰에 추가할 테스트 유저 정보를 생성한다.
    User testUser = userRepository.save(User.builder()
            .email("user@gamil.com")
            .password("test")
            .build());

    // when
    //   - 위에서 생성한 testUser에 generateToken() 메서드를 호출해 토큰을 만든다.
    String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

    // then
    //   - jjwt 라이브러리를 사용하여 토큰을 복호화한다.
    Long userId = Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey())
            .parseClaimsJws(token)
            .getBody()
            .get("id", Long.class);

    // 토큰을 만들 때 클레임으로 넣은 id값과 given절에서 만든 유저 ID가 동일한지 확인한다.
    assertThat(userId).isEqualTo(testUser.getId());
  }

  // validToken() 검증 테스트
  @DisplayName("validToken(): 만료된 토큰인 때에 유효성 검증에 실패한다.")
  @Test
  // 검증 실패를 확인하는 메소드.
  //   -
  void validToken_invalidToken() {
    // given
    //   - jjwt 라이브러리를 사용해 토큰을 생성한다.
    //   - 만료 시간을 현재 시간보다 7일 전으로 설정하여 이미 만료된 토큰을 생성한다.
    String token = JwtFactory.builder()
            .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
            .build()
            .createToken(jwtProperties);

    // when
    //   - 토큰 제공자의 validToken() 메서드를 호출하여 유효한 토큰인지 검증한다.
    boolean result = tokenProvider.validToken(token);

    // then
    //   - 반환값이 false(유효한 토큰이 아님)인 것을 확인한다.
    assertThat(result).isFalse();
  }

  @DisplayName("validToken(): 유효한 토큰인 때에 유효성 검증에 성공한다.")
  @Test
  // 검증 성공을 확인하는 메소드.
  void validToken_validToken() {
    // given
    //   - jjwt 라이브러리를 사용해 토큰을 생성한다. (만료되지 않은 토큰).
    String token = JwtFactory.withDefaultValues()
            .createToken(jwtProperties);

    // when
    //   - 토큰 제공자의 validToken() 메서드를 호출해 유효한 토큰인지 검증한다.
    boolean result = tokenProvider.validToken(token);

    // then
    //   - 결괏값이 true(유효한 토큰임)인 것을 확인한다.
    assertThat(result).isTrue();
  }

  // getAuthentication() 검증 테스트.
  //   - 토큰을 전달받아 인증 정보를 담은 객체 Authentication을 반환하는 메서드 getAuthentication()을 테스트 한다.
  @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
  @Test
  void getAuthentication() {
    // given
    //   - jjwt 라이브러리를 사용해 토큰을 생성한다.
    //     - 토큰의 제목인 subject는 "user@email.com"으로 한다.
    String userEmail = "user@email.com";
    String token = JwtFactory.builder()
            .subject(userEmail)
            .build()
            .createToken(jwtProperties);

    // when
    //   - 토큰 제공자의 getAuthentication() 메서드를 호출해 인증 객체를 반환한다.
    Authentication authentication = tokenProvider.getAuthentication(token);

    // then
    //   - 반환받은 인증 객체의 유저 이름을 가져와 given 절에서 설정한 subject 값인 "user@email.com"과 동일한지
    //     확인한다.
    assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
  }

  // getUserId() 검증 테스트.
  //   - 토큰 기반으로 유저 ID를 가져오는 메서드를 테스트한다.
  @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
  @Test
  void getUserId() {
    // given
    //   - jjwt 라이브러리를 사용해 토큰을 생성한다.
    //   - 클레임을 추가한다. (키는 "id", 값은 1인 유저 ID).
    Long userId = 1L;
    String token = JwtFactory.builder()
            .claims(Map.of("id", userId))
            .build()
            .createToken(jwtProperties);

    // when
    //   - 토큰 제공자의 getUserId() 메서드를 호출하여 유저 ID를 반환받는다.
    Long userIdByToken = tokenProvider.getUserId(token);

    // then
    //   - 반환받은 유저 ID가 given 절에서 설정한 유저 ID값인 1과 같은지 확인한다.
    assertThat(userIdByToken).isEqualTo(userId);
  }
}
