package me.shinsunyoung.springbootdeveloper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// created_at과 update_at을 자동으로 업데이트한다.
@EnableJpaAuditing
// @SptringBootApplication은 자바의 main() 메소드와 같은 역할을 한다. (스프링 부트의 시작).
@SpringBootApplication
public class SpringbootDeveloperApplication {
  // SpringApplication.run() 메소드는 애플리케이션을 실행한다.
  //   - 첫 번째 인수: 스프링 부트 4 애플리케이션의 메인 클래스로 사용할 클래스.
  //   - 두 번째 인수: 커맨드 라인의 인수들.
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDeveloperApplication.class, args);
  }

}
