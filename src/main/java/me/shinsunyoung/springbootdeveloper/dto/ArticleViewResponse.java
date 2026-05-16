package me.shinsunyoung.springbootdeveloper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.Article;
import me.shinsunyoung.springbootdeveloper.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class ArticleViewResponse {

  private Long id;
  private String title;
  private String content;
  private String imageUrl;
  private LocalDateTime createdAt;
  private String author;
  // 댓글 추가를 위해 추가.
  private List<Comment> comments;

  public ArticleViewResponse(Article article) {
    this.id = article.getId();
    this.title = article.getTitle();
    this.content = article.getTitle();
    this.author = article.getAuthor();
    this.imageUrl = article.getImageUrl();
    this.createdAt = article.getCreatedAt();
    // 댓글 추가를 위해 추가.
    this.comments = article.getComments();
  }
}
