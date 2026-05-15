package me.shinsunyoung.springbootdeveloper.config.error;

import lombok.extern.slf4j.Slf4j;
import me.shinsunyoung.springbootdeveloper.config.error.exception.BusinessBaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
// 모든 컨트롤러에서 발생하는 예외를 잡아서 처리한다.
@ControllerAdvice
public class GlobalExceptionHandler {
  // HttpRequestMethodNotSupportedException 예외를 잡아서 처리한다.
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException e) {
    log.error("HttpRequestMethodNotSupportedException", e);
    return createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
  }

  // 특정 예외 상황에 대한 처리를 정의할 수 있다.
  @ExceptionHandler(BusinessBaseException.class)
  protected ResponseEntity<ErrorResponse> handle(Exception e) {
    e.printStackTrace();
    log.error("Exception", e);
    return createErrorResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
    return new ResponseEntity<>(
            ErrorResponse.of(errorCode),
            errorCode.getStatus());
  }
}