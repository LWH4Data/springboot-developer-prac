package me.shinsunyoung.springbootdeveloper.controller;

import me.shinsunyoung.springbootdeveloper.config.error.ErrorCode;
import me.shinsunyoung.springbootdeveloper.domain.Article;
import me.shinsunyoung.springbootdeveloper.domain.User;
import me.shinsunyoung.springbootdeveloper.dto.AddArticleRequest;
import me.shinsunyoung.springbootdeveloper.dto.UpdateArticleRequest;
import me.shinsunyoung.springbootdeveloper.repository.BlogRepository;
import me.shinsunyoung.springbootdeveloper.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 테스트용 애플리케이션 컨텍스트
@SpringBootTest
// MockMvc 생성 및 자동 구성.
@AutoConfigureMockMvc
class BlogApiControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  // 직렬화, 역직렬화 클래스
  //   - 직렬화(serialization): 자바 객체 → JSON 데이터
  //   - 역직렬화(deserialization): JSON 데이터 → 자바 객체
  protected ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  BlogRepository blogRepository;

  // 인증 로직을 추가한다.
  @Autowired
  UserRepository userRepository;

  User user;

  // setAuthentication() 메서드를 통해 테스트 유저를 지정한다.
  @BeforeEach
  void setSecurityContext() {
    userRepository.deleteAll();
    user = userRepository.save(User.builder()
            .email("user@gmail.com")
            .password("test")
            .build());

    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
  }

  // 테스트 실행 전 실행하는 메소드
  @BeforeEach
  public void mockMvcSetup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .build();
    blogRepository.deleteAll();
  }

  @DisplayName("addArticle: 블로그 글 추가에 성공한다.")
  @Test
  public void addArticle() throws Exception {
    // given
    final String url = "/api/articles";
    final String title = "title";
    final String content = "content";
    final AddArticleRequest userRequest = new AddArticleRequest(title, content);
    // 객체 JSON으로 직렬화.
    //   - writeValueAsString() 메소드가 객치를 JSON으로 직렬화한다.
    final String requestBody = objectMapper.writeValueAsString(userRequest);
    // 인증관련 테스트를 위해 코드 추가.
    //   - 글 생성 API에서는 Principal 객체를 받는다.
    //   - 따라서 객체에 테스트 유저가 들어가도록 한다.
    //   - 해당 테스트 코드에서는 [Principal 객체 모킹] → [스프링 부트 getName() 메서드 호출] → [userName] 반환.
    Principal principal = Mockito.mock(Principal.class);
    Mockito.when(principal.getName()).thenReturn("username");

    // when
    // MockMvc를 통해 HTTP 메소드, URL, 요청 본문, 요청 타입 등을 설정하여 테스트 요청을 보낸다.
    ResultActions result = mockMvc.perform(post(url)
            // contentType() 메소드는 요청을 보낼 때 JOSN, XML 등 다양한 타입 중 하나를 선정하여 보낸다.
            //   - 지금은 JSON 사용.
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            // 인증 기능 테스트를 위해 코드 추가.
            .principal(principal)
            .content(requestBody));

    // then
    result.andExpect(status().isCreated());

    List<Article> articles = blogRepository.findAll();
    // assertThat 메소드로 블로그 글의 개수가 1인지 확인한다.
    assertThat(articles.size()).isEqualTo(1);
    assertThat(articles.get(0).getTitle()).isEqualTo(title);
    assertThat(articles.get(0).getContent()).isEqualTo(content);
  }

  // 모든 블로그 글 목록 조회 테스트.
  @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다.")
  @Test
  public void findAllArticles() throws Exception {
    // given
    final String url = "/api/articles";
    // 중복 코드를 제거하기 위해 글을 만드는 로직을 수정한다.
    Article savedArticle = createDefaultArticle();

    // when
    final ResultActions resultActions = mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON));

    // then
    resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
            .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
  }

  // 블로그의 단일 글 조회 API 테스트 코드를 작성한다.
  @DisplayName("findArticle: 블로그 글 조회에 성공한다.")
  @Test
  public void findArticle() throws Exception {
    // given
    final String url = "/api/articles/{id}";
    Article savedArticle = createDefaultArticle();

    // when
    final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

    // then
    resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
            .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
  }

  @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다.")
  @Test
  public void deleteArticle() throws Exception {
    // given
    final String url = "/api/articles/{id}";
    Article savedArticle = createDefaultArticle();

    // when
    mockMvc.perform(delete(url, savedArticle.getId()))
            .andExpect(status().isOk());

    // then
    List<Article> articles = blogRepository.findAll();

    assertThat(articles.isEmpty());
  }

  @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
  @Test
  public void updateArticle() throws Exception {
    // given
    final String url = "/api/articles/{id}";
    Article savedArticle = createDefaultArticle();

    final String newTitle = "new title";
    final String newContent = "new content";

    UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

    // when
    ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isOk());

    Article article = blogRepository.findById(savedArticle.getId()).get();

    assertThat(article.getTitle()).isEqualTo(newTitle);
    assertThat(article.getContent()).isEqualTo(newContent);
    }
  // 인증 기능에 필요한 메소드들을 추가한다.
  private Article createDefaultArticle() {
    return blogRepository.save(Article.builder()
            .title("title")
            .author(user.getUsername())
            .content("content")
            .build());
  }

  // 값 검증 테스트 추가.
  @DisplayName("addArticle: 아티클 추가할 때 title이 null이면 실패한다.")
  @Test
  public void addArticleNullValidation() throws Exception {
    // given
    //   - 블로그 글 추가에 필요한 요청 객체를 만든다.
    final String url = "/api/articles";
    // title은 null로 설정한다.
    final String title = null;
    final String content = "content";
    final AddArticleRequest userRequest = new AddArticleRequest(title, content);

    final String requestBody = objectMapper.writeValueAsString(userRequest);

    Principal principal = Mockito.mock(Principal.class);
    Mockito.when(principal.getName()).thenReturn("username");

    // when
    //   - 블로그 글 추가 API에 요청을 보낸다.
    ResultActions result = mockMvc.perform(post(url)
            // 요청 타입은 JSON
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            // given에서 만들어둔 객체를 요청 본문으로 함께 보낸다.
            .principal(principal)
            .content(requestBody));

    // then
    //   - 응답 코드가 400 Bad Request인지 확인한다.
    result.andExpect(status().isBadRequest());
  }

  @DisplayName("addArticle: 아티클을 추가할 때 title이 10자를 넘으면 실패한다.")
  @Test
  public void addArticleSizeValidation() throws Exception {
    // given
    //   - 블로그 글 추가에 필요한 요청 객체를 만든다.
    Faker faker = new Faker();

    final String url = "/api/articles";
    // title에는 길이 11의 문자가 들어가도록 설정한다.
    final String title = faker.lorem().characters(11);
    final String content = "content";
    final AddArticleRequest userRequest = new AddArticleRequest(title, content);

    final String requestBody = objectMapper.writeValueAsString(userRequest);

    Principal principal = Mockito.mock(Principal.class);
    Mockito.when(principal.getName()).thenReturn("username");

    // when
    //   - 블로그 글 추가 API에 요청을 보낸다.
    ResultActions result = mockMvc.perform(post(url)
            // 요청 타입은 JSON
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            // given 절에서 미리 만들어둔 객체를 요청 본문으로 함께 보낸다.
            .principal(principal)
            .content(requestBody));

    // then
    //   - 응답 코드가 400 Bad Request인지 확인한다.
    result.andExpect(status().isBadRequest());
  }

  // GlobalException 테스트를 위한 코드 작성.
  //   - GET만 있는 url에 post 요청을 보내 실패하는지 확인한다.
  @DisplayName("findArticle: 잘못된 HTTP 메서드로 아티클을 조회하려고 하면 조회에 실패한다.")
  @Test
  public void invalidHttpMethod() throws Exception {
    // given
    final String url = "/api/articles/{id}";

    // when
    final ResultActions resultActions = mockMvc.perform(post(url, 1));

    // then
    resultActions
            // andDo(print()): 실제 응답이 어떻게 나오는지 콘솔에 출력할 수 있다.
            .andDo(print())
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.message").value(ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
  }

  // 블로그 조회 예외 테스트.
  @DisplayName("findArticle: 존재하지 않는 아티클을 조회하려고 하면 조회에 실패한다.")
  @Test
  public void findArticleInvalidArticle() throws Exception {
    // given
    final String url = "/api/articles/{id}";
    final long invalidId = 1;

    // when
    final ResultActions resultActions = mockMvc.perform(get(url, invalidId));

    // then
    resultActions
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.ARTICLE_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.code").value(ErrorCode.ARTICLE_NOT_FOUND.getCode()));
  }
}