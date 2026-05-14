package me.shinsunyoung.springbootdeveloper.controller;

import me.shinsunyoung.springbootdeveloper.dto.UploadResponse;
import me.shinsunyoung.springbootdeveloper.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UploadController {

  private final FileStorageService fileStorageService;

  public UploadController(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  @PostMapping("/api/upload")
  // MultipartFile은 Spring에서 제공하는 타입이다.
  //   - multipart/form-data 요청으로 들어온 파일을 객체로 받아,
  //   - 파일 내용, 원본 파일명, 크기, 타입 등에 메서드로 쉽게 접근할 수 있다.
  public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file")
                                                   MultipartFile file) throws IOException {

    // 로컬 PC에 저장한 뒤 저장한 경로를 반환한다.
    //   - ResponseEntity.status(HttpStatus.CREATED)는 HTTP 응답의 상태 코드를
    //     201 Created로 설정하겠다는 뜻이다.
    //   - save() 메소드는 실제로 저장을 하고 저장한 경로를 반환한다.
    //   - save()에서 반환된 결과를 body()로 하여 반환한다.
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(fileStorageService.store(
                    file.getBytes(),
                    file.getOriginalFilename()
            ));
  }
}
