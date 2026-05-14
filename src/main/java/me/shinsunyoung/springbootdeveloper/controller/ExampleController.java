package me.shinsunyoung.springbootdeveloper.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

// 컨트롤러임을 명시한다.
@Controller
public class ExampleController {

  @GetMapping("/thymeleaf/example")
  // 뷰로 데이터를 넘겨주는 모델 객체를 통해 생성한다.
  //   - Model 객체는 뷰(HTML)로 값을 넘겨주는 객체이다.
  //   - Model 객체는 따로 생성할 필요 없이 코드처럼 인자로 선언하면 스프링이 알아서 만들어준다.
  public String thymeleafExample(Model model) {
    Person examplePerson = new Person();
    examplePerson.setId(1L);
    examplePerson.setName("홍길동");
    examplePerson.setAge(11);
    examplePerson.setHobbies(List.of("운동", "독서"));

    // Person 객체 저장
    //   - addAttribute() 메소드는 모델에 값을 저장한다.
    // person이라는 키에 examplePerson 저장.
    model.addAttribute("person", examplePerson);
    // today라는 키에 날짜 정보를 저장.
    model.addAttribute("today", LocalDate.now());

    // example.html 이라는 뷰 조회
    //   - @Controller 애너테이션으로인해 뷰의 이름을 반환하는 것으로 된다.
    //     → 즉, 이 이름의 뷰 파일을 찾아라는 것을 의미한다.
    //     → resource/templates 디렉터리에서 example.html을 찾아 웹 브라우저에 해당 파일을 보여준다.
    return "example";
  }

  @Setter
  @Getter
  class Person {
    private Long id;
    private String name;
    private int age;
    private List<String> hobbies;
  }
}
