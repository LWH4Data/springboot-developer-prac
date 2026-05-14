package me.shinsunyoung.springbootdeveloper.repository;

import me.shinsunyoung.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  // email로 사용자 정보를 가져온다.
  //   - 스프링 데이터 JPA가 쿼리를 자동 생성해줌을 이용한다.
  Optional<User> findByEmail(String email);
}
