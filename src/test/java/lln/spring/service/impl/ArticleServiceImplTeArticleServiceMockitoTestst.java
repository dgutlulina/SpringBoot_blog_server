package lln.spring.service.impl;

import lln.spring.mapper.ArticleMapper;
import lln.spring.entity.Article;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

@SpringBootTest
class ArticleServiceImplTeArticleServiceMockitoTestst {
    @Mock
    private ArticleMapper articleMapper;
    @InjectMocks
    private ArticleServiceImpl articleService;
    @Test
    void publish() {
        Article article = new Article();
        article.setContent("新闻内容");
        article.setTitle("新闻标题");
        article.setTags("新闻标签");
        articleService.publish(article);
        Mockito.verify(articleMapper, times(1)).insert(article);
        assertEquals("新闻标签", article.getTags());
    }
}
