package me.shinsunyoung.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.config.error.exception.ArticleNotFoundException;
import me.shinsunyoung.springbootdeveloper.domain.Article;
import me.shinsunyoung.springbootdeveloper.dto.AddArticleRequest;
import me.shinsunyoung.springbootdeveloper.dto.UpdateArticleRequest;
import me.shinsunyoung.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

// (롬복) final이 붙거나 @NotNull이 붙은 필드의 생성자 추가.
@RequiredArgsConstructor
// 빈으로 등록.
@Service
public class BlogService {

  private final BlogRepository blogRepository;

  // 블로그 글 추가 메소드.
  //   - save는 JpaRepository에서 지원하는 저장 메소드이다.
  //   - AddArticleRequest 클래스에 저장된 값들을 article DB에 저장한다.
  //   - userName을 받도록 수정.
  public Article save(AddArticleRequest request, String userName) {
    return blogRepository.save(request.toEntity(userName));
  }

  // DB의 모든 글을 가져오는 findAll() 메소드 추가.
  //   - JPA 지원 메소드 findAll()을 호출하여 article 테이블의 모든 데이터를 조회한다.
  public List<Article> findAll() {
    return blogRepository.findAll();
  }

  // DB에서 블로그 글 하나를 조회하는 메소드를 작성한다.
  //   - JPA의 findById() 메소드를 활용하여 id 기반으로 작성한다.
  //   - 소속된 객체가 다르기 때문에 같은 이름의 메소드 정의가 가능하다.
  public Article findById(long id) {
    return blogRepository.findById(id)
            .orElseThrow(ArticleNotFoundException::new);
  }

  // 토큰을 활용한 DB 데이터 삭제 방식으로 변경한다.
  public void delete(long id) {
    Article article = blogRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

    authorizeArticleAuthor(article);
    blogRepository.delete(article);
  }

//  // id를 활용하여 DB의 데이터를 삭제한다.
//  public void delete(long id) {
//    blogRepository.deleteById(id);
//  }
//
//  // 글을 수정하는 update() 메소드.
//  //   - @Transactional을 사용하여 조회한 엔티티를 영속 상태로 유지한다.
//  //     - 수정 도중에 종료되면 트랜잭션이 종료된 것이 아니기에 반영되지 않는다.
//  //   - 트랜잭션 종료 시 JPA의 변경 감지로 수정 내용이 DB에 반영된다.

  // 토큰을 활용해 DB 데이터를 업데이트하도록 수정.
  @Transactional
  public Article update(long id, UpdateArticleRequest request) {
    Article article = blogRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

    authorizeArticleAuthor(article);
    article.update(request.getTitle(), request.getContent(), request.getImageUrl());

    return article;
  }

//  @Transactional
//  public Article update(long id, UpdateArticleRequest request) {
//    Article article = blogRepository.findById(id)
//            .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
//
//    article.update(request.getTitle(), request.getContent(), request.getImageUrl());
//
//    return article;
//  }

  // 게시글을 작성한 유저인지 확인.
  //   - 수정, 삭제 메서드에서 현재 인증 객체에 담겨 있는 사용자의 정보와 글을 작성한 사용자의 정보를 비교한다.
  private static void authorizeArticleAuthor(Article article) {
    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
    if (!article.getAuthor().equals(userName)) {
      throw new IllegalArgumentException("not authorized");
    }
  }
}
