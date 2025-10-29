package lln.spring.service.impl;

import lln.spring.mapper.ArticleMapper;
import lln.spring.entity.Article;
import lln.spring.service.ArticleService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ArticleServiceImplOnlyJunit5Test {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;
    private static Article article;
    @BeforeAll
    static void testBeforeAll() {
        article = new Article();
        article.setContent("新闻内容");
        article.setTitle("新闻标题");
        article.setTags("新闻标签");
    }
    @Test
    void publish() {
        int oldRecords = Math.toIntExact(articleMapper.selectCount(null));
        articleService.publish(article);
        int newRecords = Math.toIntExact(articleMapper.selectCount(null));
        assertEquals(oldRecords + 100, newRecords);
    }
}