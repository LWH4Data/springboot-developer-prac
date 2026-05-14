package me.shinsunyoung.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.shinsunyoung.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.shinsunyoung.springbootdeveloper.dto.CreateAccessTokenResponse;
import me.shinsunyoung.springbootdeveloper.service.RefreshTokenService;
import me.shinsunyoung.springbootdeveloper.service.TokenService;
import me.shinsunyoung.springbootdeveloper.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
  private final TokenService tokenService;
  private final RefreshTokenService refreshTokenService;

  @PostMapping("/api/token")
  public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken
          (@RequestBody CreateAccessTokenRequest request) {
    try {
      String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
      return ResponseEntity.status(HttpStatus.CREATED)
              .body(new CreateAccessTokenResponse(newAccessToken));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @DeleteMapping("/api/refresh-token")
  public ResponseEntity<Void> deleteRefreshToken(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = WebUtils.getCookie(request, "refresh_token");

    if (cookie != null && cookie.getValue() != null && !cookie.getValue().isBlank()) {
      refreshTokenService.deleteByRefreshToken(cookie.getValue());
    }

    CookieUtil.deleteCookie(request, response, "refresh_token");
    return ResponseEntity.ok().build();
  }
}
