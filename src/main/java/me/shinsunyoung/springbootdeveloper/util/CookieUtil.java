package me.shinsunyoung.springbootdeveloper.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
  // 요청값(이름, 값, 만료 기간)을 바탕으로 쿠키 추가.
  public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  // 쿠키의 이름을 입력받아 쿠키 삭제.
  //   - 실제로 삭제하는 방법은 없기 때문에 파라미터로 넘어온 키의 쿠키를 빈 값으로 바꾸고 만료 시간을
  //     0으로 설정하여 쿠키가 재생성되자마자 만료 처리한다.
  public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return;
    }

    // 요청에 담긴 쿠키들을 하나씩 확인한다.
    for (Cookie cookie : cookies) {

      // 삭제하려는 이름의 쿠키인지 확인한다.
      if (name.equals(cookie.getName())) {

        // 쿠키 값을 빈 문자열로 변경한다.
        // 단, 이것만으로는 브라우저의 쿠키가 삭제되지 않는다.
        cookie.setValue("");

        // 쿠키를 생성했을 때와 같은 path를 지정해야
        // 브라우저가 기존 쿠키와 같은 쿠키로 인식하고 삭제할 수 있다.
        cookie.setPath("/");

        // 쿠키의 유효 시간을 0초로 설정한다.
        // Max-Age=0은 브라우저에게 "이 쿠키를 즉시 만료시켜라"는 의미다.
        cookie.setMaxAge(0);

        // 변경된 쿠키 정보를 응답에 추가한다.
        // 실제로는 Set-Cookie 헤더를 통해 브라우저에게 쿠키 삭제 명령을 보내는 것이다.
        //   - addCookie(): 요청값(이름, 값, 만료 기간)을 바탕으로 HTTP 응답에 쿠키를 추가한다.
        response.addCookie(cookie);
      }
    }
  }

  // 객체를 직렬화해 쿠키의 값으로 변환.
  public static String serialize(Object obj) {
    return Base64.getUrlEncoder()
            // serialize(): 객체를 직렬화하여 쿠키의 값에 들어갈 값으로 변환한다.
            .encodeToString(SerializationUtils.serialize(obj));
  }

  // 쿠키를 역직렬화해 객체로 변환.
  public static <T> T deserialize(Cookie cookie, Class<T> cls) {
    return cls.cast(
            // deserialize(): 쿠키를 역질렬화 객체로 변환한다.
            SerializationUtils.deserialize(
                    Base64.getUrlDecoder().decode(cookie.getValue())
            )
    );
  }
}
