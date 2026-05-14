package me.shinsunyoung.springbootdeveloper.service;

import me.shinsunyoung.springbootdeveloper.dto.UploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// local 프로파일에서만 실행되도록 코드 수정.
@Profile("local")
@Service
public class LocalFileStorageService implements FileStorageService {

  private final Path uploadDir;

  public LocalFileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
    this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
  }

  @Override
  public UploadResponse store(byte[] bytes, String filename) {
    try {
      Files.createDirectories(uploadDir);

      String ext = filename.contains(".")
              ? filename.substring(filename.lastIndexOf('.'))
              : ".png"; // 확장자 추출.

      String saved = UUID.randomUUID() + ext;

      // 업로드 디렉터리에 파일 저장.
      Files.write(uploadDir.resolve(saved), bytes);

      return new UploadResponse("/uploads/" + saved);
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }
}
