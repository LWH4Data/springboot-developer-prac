package me.shinsunyoung.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.Article;

// 기본 생성자 추가.
//   - JPA는 DB에서 데이터를 조회하고 객체를 만들 때 우선 파라미터가 없는
//     기본 생성자를 생성하기에 필요하다.
//   - 일반적으로 기본 생성자는 비어있는 껍데기이다.
@NoArgsConstructor
// 모든 필드 값을 파라미터로 받는 생성자 추가.
@AllArgsConstructor
@Getter
public class AddArticleRequest {

  private String title;
  private String content;
  // 이미지 url 추가.
  private String imageUrl;

  // 이미지 url을 위한 생성자 정의.
  public AddArticleRequest(String title, String content) {
    this.title = title;
    this.content = content;
  }

  // 빌더 패턴을 통해 생성자로 객체 생성.
  //   - toEntity(): 빌터 패턴을 사용해 DTO를 엔티티로 만들어주는 메소드이다.
  //   - 블로그 글을 추가할 때 저장할 엔티티로 변환하는 용도이다.
  public Article toEntity(String author) {
    return Article.builder()
            .title(title)
            .content(content)
            // 글쓴이 추가.
            .author(author)
            // 이미지 url을 builder에 추가.
            .imageUrl(imageUrl)
            .build();
  }
}
