package me.shinsunyoung.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.Article;
import me.shinsunyoung.springbootdeveloper.domain.Comment;
import me.shinsunyoung.springbootdeveloper.dto.*;
import me.shinsunyoung.springbootdeveloper.service.BlogService;
import me.shinsunyoung.springbootdeveloper.service.ThumbnailGeneratorService;
import me.shinsunyoung.springbootdeveloper.service.WritingAssistantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
// HTTP Response Body의 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
@RestController

public class BlogApiController {

  private final BlogService blogService;
  private final WritingAssistantService writingAssistantService;
  // 섬네일 AI 생성을 위한 변수 선언.
  private final ThumbnailGeneratorService thumbnailGeneratorService;

  // HTTP 메소드가 POST일 때 전달받은 URL과 동일하면 메소드로 매핑.
  @PostMapping("/api/articles")

  // @RequestBody로 요청 본문 값 매핑.
  public ResponseEntity<Article> addArticle(@RequestBody @Validated AddArticleRequest request, Principal principal) {
    Article savedArticle = blogService.save(request, principal.getName());

    // 요청한 자원이 성공적으로 생성되었으면 저장된 블로그 글 정보를 응답 객체에 담아 전송.
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(savedArticle);
  }

  // 사용자 요청을 받아 AI로작성 처리하는 메소드 작성.
  @PostMapping("/api/ai-suggestions")
  public ResponseEntity<WritingSuggestionResponse> writingAssist(@RequestBody WritingSuggestionRequest request) {
    WritingSuggestionResponse response = writingAssistantService.getWritingAssist(request);

    return ResponseEntity.ok()
            .body(response);
  }

  // AI 섬네일 생성을 위한 요청 처리 메소드 작성.
  @PostMapping("/api/ai-thumbnails")
  public ResponseEntity<GeneratorThumbnailResponse> thumbnailGenerator(@RequestBody GeneratorThumbnailRequest request) {
    GeneratorThumbnailResponse response = thumbnailGeneratorService.generateThumbnail(request);

    return ResponseEntity.ok()
            .body(response);
  }

  // 댓글 추가 요청 처리.
  //   - 요청한 블로그 글 아이디로 블로그 글을 찾아 댓글 내용,
  @PostMapping("/api/comments")
  public ResponseEntity<AddCommentResponse> addComment(@RequestBody AddCommentRequest request, Principal principal) {
    Comment savedComment = blogService.addComment(request, principal.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(new AddCommentResponse(savedComment));
  }

  // 전체 게시글 조회 API
  // GET /api/articles 요청이 들어오면 이 메서드가 실행된다.
  @GetMapping("/api/articles")
  public ResponseEntity<List<ArticleResponse>> findAllArticles() {

    // blogService.findAll()
    // - DB에서 전체 Article 엔티티 목록을 조회한다.
    //
    // .stream()
    // - 조회된 List<Article>을 Stream으로 변환한다.
    // - Stream을 사용하면 map, filter 같은 연속 처리를 할 수 있다.
    //
    // .map(ArticleResponse::new)
    // - Article 엔티티 하나하나를 ArticleResponse DTO로 변환한다.
    // - ArticleResponse::new는 new ArticleResponse(article)과 같은 의미이다.
    //
    // .toList()
    // - 변환된 ArticleResponse 객체들을 다시 List로 모은다.
    List<ArticleResponse> articles = blogService.findAll()
            .stream()
            .map(ArticleResponse::new)
            .toList();

    // ResponseEntity.ok()
    // - HTTP 상태 코드 200 OK 응답을 만든다.
    //
    // .body(articles)
    // - 응답 본문에 articles 데이터를 담아서 반환한다.
    return ResponseEntity.ok()
            .body(articles);
  }

  @GetMapping("/api/articles/{id}")
  // URL 경로에서 값 추출.
  //   - @PathVariable: URL에서 값을 가져오는 애너테이션이다.
  //     - "api/articles/3 GET" 요청이 들어오면 id는 3이 된다.
  public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
    Article article = blogService.findById(id);

    return ResponseEntity.ok()
            .body(new ArticleResponse(article));
  }

  @DeleteMapping("/api/articles/{id}")
  public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
    blogService.delete(id);

    return ResponseEntity.ok()
            .build();
  }

  @PutMapping("/api/articles/{id}")
  public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                               @RequestBody UpdateArticleRequest request) {

    // update() 메소드가 수정을 처리하고 응답을 반환.
    Article updateArticle = blogService.update(id, request);

    // 응답은 body에 담아서 클라이언트에 전달.
    return ResponseEntity.ok()
            .body(updateArticle);
  }
}
