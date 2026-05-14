package me.shinsunyoung.springbootdeveloper.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.dto.AddUserRequest;
import me.shinsunyoung.springbootdeveloper.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserApiController {

  private final UserService userService;

  // 회원 가입을 위한 메소드를 구현한다.============================================
  @PostMapping("/user")
  public String signup(AddUserRequest request) {
    // 회원 가입 메소드 호출.
    userService.save(request);
    // 회원 가입이 완료된 이후에 로그인 페이지로 이동.
    //   - "redirect:" 접두사를 활용한다.
    return "redirect:/login";
  }

  // 로그아웃을 위한 메소드를 구현한다.=============================================
  @GetMapping
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    new SecurityContextLogoutHandler().logout(request, response,
            SecurityContextHolder.getContext().getAuthentication());

    return "redirect:/login";
  }
}
