package me.shinsunyoung.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "author", nullable = false)
  private String author;

  @Column(name = "content", nullable = false)
  private String content;

  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // @ManyToOne: 테이블 간의 N:1 관계를 나타내기 위해 사용하는 애너테이션이다.
  //   - 현재 작성 중인 코드는 Comment이고, comment가 many이기에 Many가 먼저 온다.
  //   - 반면 Article에서는 article이 하나이기에 OneToMany가 된다.
  @ManyToOne
  private Article article;

  @Builder
  public Comment(Article article, String author, String content) {
    this.article = article;
    this.author = author;
    this.content = content;
  }
}
