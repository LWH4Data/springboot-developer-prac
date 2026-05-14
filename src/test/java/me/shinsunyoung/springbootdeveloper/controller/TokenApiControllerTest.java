package me.shinsunyoung.springbootdeveloper.controller;

import me.shinsunyoung.springbootdeveloper.config.jwt.JwtProperties;
import me.shinsunyoung.springbootdeveloper.controller.config.jwt.JwtFactory;
import me.shinsunyoung.springbootdeveloper.domain.RefreshToken;
import me.shinsunyoung.springbootdeveloper.domain.User;
import me.shinsunyoung.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.shinsunyoung.springbootdeveloper.repository.RefreshTokenRepository;
import me.shinsunyoung.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
// 보통은 아래 ObjectMapper를 많이 사용한다.
// import com.fasterxml.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * 테스트 전체 흐름
 *
 * [테스트용 User 저장]
 *      ↓
 * [User id를 담은 refresh token 생성]
 *      ↓
 * [refresh token을 DB에 저장]
 *      ↓
 * [refresh token을 요청 DTO에 담아 JSON으로 변환]
 *      ↓
 * [POST /api/token 요청]
 *      ↓
 * [새 access token이 응답으로 오는지 검증]
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {

  @Autowired
  // MockMvc: 실제 서버를 띄우지 않고 Controller 요청/응답을 테스트할 수 있게 해주는 객체.
  // protected: 같은 패키지 또는 상속받은 자식 클래스에서 접근 가능하다.
  // private: 같은 클래스 내부에서만 접근 가능하다.
  protected MockMvc mockMvc;

  @Autowired
  // ObjectMapper: Java 객체를 JSON 문자열로 바꾸거나, JSON을 Java 객체로 바꿀 때 사용한다.
  // 예: CreateAccessTokenRequest 객체 -> {"refreshToken":"..."}
  protected ObjectMapper objectMapper;

  @Autowired
  // WebApplicationContext: Spring 웹 애플리케이션의 Bean과 설정 정보를 담고 있는 컨텍스트.
  // MockMvc를 웹 환경 기준으로 직접 구성할 때 사용한다.
  private WebApplicationContext context;

  @Autowired
  // JwtProperties: application.yml/properties에 정의된 JWT issuer, secret key 같은 설정값을 담는 객체.
  JwtProperties jwtProperties;

  @Autowired
  // UserRepository: 테스트용 사용자를 DB에 저장하거나 삭제할 때 사용한다.
  UserRepository userRepository;

  @Autowired
  // RefreshTokenRepository: refresh token을 DB에 저장하거나 조회할 때 사용한다.
  RefreshTokenRepository refreshTokenRepository;

  @BeforeEach
  public void mockMvcSetUp() {
    // 각 테스트 실행 전에 MockMvc를 현재 Spring 웹 컨텍스트 기준으로 다시 설정한다.
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .build();

    // 테스트 간 데이터 충돌을 막기 위해 User 테이블을 비운다.
    userRepository.deleteAll();

    // 필요하면 refresh token도 같이 비우는 것이 안전하다.
    // refreshTokenRepository.deleteAll();
  }

  @DisplayName("createNewAccessToken: 새로운 액세스 토큰을 발급한다.")
  @Test
  public void createNewAccessToken() throws Exception {
    // given
    // 테스트할 API 주소.
    final String url = "/api/token";

    // 1. 테스트용 사용자를 DB에 저장한다.
    // save() 반환값에는 DB에서 생성된 id가 들어있다.
    User testUser = userRepository.save(User.builder()
            .email("user@gmail.com")
            .password("test")
            .build());

    // sample:
    // testUser.getId() == 1L 이라고 가정하면,
    // JWT claims 안에 {"id": 1} 형태로 사용자 id가 들어간다.

    // 2. 테스트용 refresh token을 생성한다.
    // claims에 user id를 넣어, 나중에 토큰에서 사용자를 식별할 수 있게 한다.
    String refreshToken = JwtFactory.builder()
            .claims(Map.of("id", testUser.getId()))
            .build()
            .createToken(jwtProperties);

    // sample:
    // refreshToken = "eyJhbGciOiJIUzI1NiJ9..."

    // 3. 생성한 refresh token을 DB에 저장한다.
    // 실제 서비스에서는 refresh token이 DB에 존재해야 access token 재발급이 가능하다.
    refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

    // 4. API 요청 body로 보낼 DTO를 만든다.
    CreateAccessTokenRequest request = new CreateAccessTokenRequest();
    request.setRefreshToken(refreshToken);

    // 5. DTO 객체를 JSON 문자열로 변환한다.
    // sample:
    // {
    //   "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
    // }
    final String requestBody = objectMapper.writeValueAsString(request);

    // when
    // 6. POST /api/token 요청을 보낸다.
    // contentType은 요청 body가 JSON임을 의미한다.
    ResultActions resultActions = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody));

    // then
    // 7. 응답 상태가 201 Created인지 확인한다.
    // 8. 응답 JSON 안에 accessToken 값이 비어 있지 않은지 확인한다.
    resultActions
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").isNotEmpty());
  }
}