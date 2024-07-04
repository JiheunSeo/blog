package com.test.blog.controller;

import com.test.blog.domain.Article;
import com.test.blog.dto.AddArticleRequest;
import com.test.blog.dto.ArticleResponse;
import com.test.blog.dto.UpdateArticleRequest;
import com.test.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController //HTTP응답으로 객체 데이터를 JSON형식으로 반환
public class BlogApiController {
    private final BlogService blogService;

    @PostMapping("/api/articles")
    public ResponseEntity addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        //요청된 자원이 성공적으로 생성되었으며 저장된 블로그 글 정보를 응답객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle); //201
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                //가져온 글 목록을 스트림으로 변환
                .map(ArticleResponse::new)
                //article -> new ArticleResponse(article)의 의미임
                .toList();
                //변환된 객체 리스트로 모으기
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/api/articles/{id}")
    //url 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticleById(@PathVariable Long id) {
        Article article = blogService.findById(id);
        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticleById(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticleById(@PathVariable Long id,
                                                             @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);
        return ResponseEntity.ok()
                .body(updatedArticle);
    }
}
