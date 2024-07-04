package com.test.blog.repository;

import com.test.blog.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
//JpaRepository<엔티티 Class name, PK 타입>

}
