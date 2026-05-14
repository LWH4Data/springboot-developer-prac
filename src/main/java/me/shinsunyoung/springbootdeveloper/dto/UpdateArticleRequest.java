package me.shinsunyoung.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
// 제목과 내용을 수정하기에 여기에 맞게 DTO를 구성.
public class UpdateArticleRequest {
  private String title;
  private String content;
  // 이미지 url 추가.
  private String imageUrl;

  // 생성자 선언.
  public UpdateArticleRequest(String title, String content) {
    this.title = title;
    this.content = content;
  }
}
