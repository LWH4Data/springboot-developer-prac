package me.shinsunyoung.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// < 롬복 사용 O > ======================================================================
// - Getter와 protected 생성자 작성을 대체한다.

// 엔티티의 생성 및 수정 시간을 자동으로 감시하고 기록한다.
@EntityListeners(AuditingEntityListener.class)
// 엔티티로 지정: DB의 테이블과 매핑할 것임.
@Entity
// Getter 대체.
@Getter
// protected 생성자 대체.
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Article {

  // id 필드를 기본키로 지정.
  @Id
  // 기본키를 자동으로 1씩 증가: IDENTITY
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false)
  private Long id;

  // 'title'이라는 not null 컬럼과 매핑
  @Column(name = "title", nullable = false)
  private String title;

  // 'content'라는 not null 컬럼과 매핑
  @Column(name = "content", nullable = false)
  private String content;

  // 이미지 url을 위한 컬럼 추가.
  @Column(name = "image_url")
  private String imageUrl;

  // 글쓴이 컬럼 추가.
  @Column(name = "author", nullable = false)
  private String author;

  // 엔티티가 생성될 때 생성 시간 저장.
  @CreatedDate
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // 엔티티가 수정될 때 수정 시간 저장.
  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 빌더 패턴으로 객체 생성.
  //   - 롬복에서 지원하는 애너테이션이다.
  //   - 생성자 위에 입력하면 빌더 패턴 방식으로 객체를 생성할 수 있다.
  //   - 빌더 패턴이란 어느 필드에 어떤 값이 들어가는지 명시적으로 파악할 수 있는 디자인 패턴이다.
  @Builder
  public Article(String author, String title, String content, String imageUrl) {
    // 글쓴이 초기화.
    this.author = author;
    this.title = title;
    this.content = content;
    // 이미지 url 초기화.
    this.imageUrl = imageUrl;
  }

  public void update(String title, String content, String imageUrl) {
    this.title = title;
    this.content = content;
    // 업데이트 메소드에도 imageUrl 추가.
    this.imageUrl = imageUrl;
  }
}

// < 롬복 사용 X > ======================================================================
// - 직접 Getter와 protected 생성자를 작성한다.
//
// 엔티티로 지정: DB의 테이블과 매핑할 것임.
//@Entity
//public class Article {
//
//  // id 필드를 기본키로 지정.
//  @Id
//  // 기본키를 자동으로 1씩 증가: IDENTITY
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @Column(name = "id", updatable = false)
//  private Long id;
//
//  // 'title'이라는 not null 컬럼과 매핑
//  @Column(name = "title", nullable = false)
//  private String title;
//
//  // 'content'라는 not null 컬럼과 매핑
//  @Column(name = "content", nullable = false)
//  private String content;
//
//  // 빌더 패턴으로 객체 생성.
//  //   - 롬복에서 지원하는 애너테이션이다.
//  //   - 생성자 위에 입력하면 빌더 패턴 방식으로 객체를 생성할 수 있다.
//  //   - 빌더 패턴이란 어느 필드에 어떤 값이 들어가는지 명시적으로 파악할 수 있는 디자인 패턴이다.
//  @Builder
//  public Article(String title, String content) {
//    this.title = title;
//    this.content = content;
//  }
//
//  // 기본 생성자.
//  protected Article() {
//  }
//
//  // Getter
//  public Long getId() {
//    return id;
//  }
//
//  public String getTitle() {
//    return title;
//  }
//
//  public String getContent() {
//    return content;
//  }
//}
