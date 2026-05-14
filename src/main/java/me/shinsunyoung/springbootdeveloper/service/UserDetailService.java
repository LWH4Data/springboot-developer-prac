package me.shinsunyoung.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class UserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  // 사용자 이름(email)으로 사용자의 정보를 가져오는 메소드.
  // 필수로 구현해야 하는 loadUserByUsername() 메소드를 오버라이딩 하여 사용자 정보를 가져오는 로직을 작성한다.
  @Override
  public UserDetails loadUserByUsername(String email) {
    return userRepository.findByEmail(email).
            orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
