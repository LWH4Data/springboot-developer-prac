package me.shinsunyoung.springbootdeveloper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {
  // "/login"에 접근하면 login.html를 반환한다.
  @GetMapping("/login")
  public String login() {
    return "oauthLogin";
  }

  // "/signup"에 접근하면 signup.html을 반환한다.
  @GetMapping("signup")
  public String signup() {
    return "signup";
  }
}
