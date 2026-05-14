package me.shinsunyoung.springbootdeveloper.repository;

import me.shinsunyoung.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository를 통해 DB 작업을 위한 기본 기능을 사용할 수 있도록 한다.
public interface BlogRepository  extends JpaRepository<Article, Long> {
}
