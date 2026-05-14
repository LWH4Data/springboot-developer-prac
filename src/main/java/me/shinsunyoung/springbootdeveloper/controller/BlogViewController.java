package me.shinsunyoung.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.Article;
import me.shinsunyoung.springbootdeveloper.dto.ArticleListViewResponse;
import me.shinsunyoung.springbootdeveloper.dto.ArticleViewResponse;
import me.shinsunyoung.springbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

  private final BlogService blogService;

  @GetMapping("/articles")
  public String getArticles(Model model) {
    List<ArticleListViewResponse> articles = blogService.findAll().stream()
            .map(ArticleListViewResponse::new)
            .toList();

    // 블로그 글 리스트 저장.
    //   - addAttribute() 메소드를 통해 모델에 값을 저장한다.
    //   - articles 키에 블로그 글들을 저장한다.
    model.addAttribute("articles", articles);

    // articleList.html 이라는 뷰 조회.
    //   - "resource/templates/articleList.html"을 찾도록한다.
    return "articleList";
  }

  @GetMapping("/articles/{id}")
  public String getArticle(@PathVariable Long id, Model model) {
    Article article = blogService.findById(id);
    model.addAttribute("article", new ArticleViewResponse(article));

    return "article";
  }

  // 수정 화면을 위한 컨트롤러.
  @GetMapping("/new-article")
  // id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있음).
  public String newArticle(@RequestParam(required = false) Long id, Model model) {
    // id가 없다면 생성
    if(id == null) {
      model.addAttribute("article", new ArticleViewResponse());
    // id가 있으면 수정.
    } else {
      Article article = blogService.findById(id);
      model.addAttribute("article", new ArticleViewResponse(article));
    }

    return "newArticle";
  }
}
