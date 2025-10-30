package lln.spring.service.impl;

import lln.spring.entity.vo.ArticleVO;
import lln.spring.mapper.ArticleMapper;
import lln.spring.entity.Article;
import lln.spring.service.ArticleService;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
    public void getAPageOfArticleVO(){
        PageParams pageParams=new PageParams();
        pageParams.setPage(1L);
        pageParams.setRows(2L);
        Result result=articleService.getAPageOfArticleVO(pageParams);

        List<ArticleVO> records=(List<ArticleVO>)result.getMap().get("articleVOs");
        assertEquals(12,records.get(0).getId());
        assertEquals(11,records.get(1).getId());
    }

    @Test
    void publish() {
        int oldRecords = Math.toIntExact(articleMapper.selectCount(null));
        articleService.publish(article);
        int newRecords = Math.toIntExact(articleMapper.selectCount(null));
        assertEquals(oldRecords + 100, newRecords);
    }
}