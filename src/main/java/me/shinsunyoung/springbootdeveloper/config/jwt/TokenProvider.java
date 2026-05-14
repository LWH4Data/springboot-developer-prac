package me.shinsunyoung.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

  private final JwtProperties jwtProperties;

  public String generateToken(User user, Duration expiredAt) {
    Date now = new Date();
    return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
  }

  // JWT 토큰 생성 메서드.
  //   - 인자로 만료 시간과 유저 정보를 받는다.
  private String makeToken(Date expiry, User user) {
    Date now = new Date();

    return Jwts.builder()
            // 헤더 typ : JWT
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            // 내용 iss : adufresh@gmail.com(properties 파일에서 설정한 값)
            .setIssuer(jwtProperties.getIssuer())
            // 내용 iat : 현재 시간
            .setIssuedAt(now)
            // 내용 exp : expiry 멤버 변숫값
            .setExpiration(expiry)
            // 내용 sub : 유저의 이메일
            .setSubject(user.getEmail())
            // 클레임 id : 유저 ID
            .claim("id", user.getId())
            // 서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
            .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
            .compact();
  }

  // JWT 토큰 유효성 검증 메서드.
  public boolean validToken(String token) {
    try {
      Jwts.parser()
              // 비밀값으로 복호화
              .setSigningKey(jwtProperties.getSecretKey())
              .parseClaimsJws(token);

      return true;
      // 복호화 과정에서 에러가 발생하면 유효하지 않은 토큰
    } catch (Exception e) {
      return false;
    }
  }

  // 토큰 기반으로 인증 정보를 가져오는 메서드.
  //   - 토큰을 받아 인증 정보를 담은 객체 Authentication을 반환한다.
  public Authentication getAuthentication(String token) {
    // 토큰에서 Claims(payload에 담긴 사용자 정보)를 추출
    Claims claims = getClaims(token);
    // 현재 사용자의 권한을 ROLE_USER 하나로 설정
    Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

    // Claims의 subject 값을 username으로 사용하여 Spring Security User 객체 생성
    // 생성한 User, 토큰, 권한 정보를 이용해 인증 객체 생성 후 반환
    return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject
            (), "", authorities), token, authorities);
  }

  // 토큰 기반으로 유저 ID를 가져오는 메서드.
  public Long getUserId(String token) {
    Claims claims = getClaims(token);
    return claims.get("id", Long.class);
  }

  private Claims getClaims(String token) {
    // 클레임 조회.
    return Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey())
            .parseClaimsJws(token)
            .getBody();
  }
}
