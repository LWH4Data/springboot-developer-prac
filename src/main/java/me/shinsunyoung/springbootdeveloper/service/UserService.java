package me.shinsunyoung.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.User;
import me.shinsunyoung.springbootdeveloper.dto.AddUserRequest;
import me.shinsunyoung.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
  private final UserRepository userRepository;
//  private final PasswordEncoder passwordEncoder;

  public Long save(AddUserRequest dto) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return userRepository.save(User.builder()
            .email(dto.getEmail())
            // 패스워드 암호화.
            //   - 패스워드 저장시 시큐리티를 설정하여 패스워드 인코딩용으로 등록한 빈을 사용해 암호화한 뒤 저장한다.
//            .password(passwordEncoder.encode(dto.getPassword()))
            .build()).getId();
  }

  // 전달받은 유저 아이디로 ID로 유저를 검색해서 전달하는 findById() 메서드 작성.
  public User findById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
  }

  // findByEmail(): 이메일을 입력받아 usres 테이블에서 유저를 찾는다.
  // 유적가 없다면 예외를 발생시킨다.
  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
  }
}
