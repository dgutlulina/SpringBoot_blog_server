package lln.spring.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lln.spring.entity.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerMockMVCTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void publishArticle() throws Exception {
        Article article = new Article();
        article.setTitle("新闻内容");
        article.setContent("新闻详情");
        article.setTags("新闻标签");

        String articleJsonString = objectMapper.writeValueAsString(article);

        mockMvc.perform(post("/article/publishArticle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.title").value("新闻内容"));

        article.setTitle("测试");
        articleJsonString = objectMapper.writeValueAsString(article);

        mockMvc.perform(post("/article/publishArticle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.title").value("测试"));
    }

    @Test
    void upload() {
    }

    @Test
    void selectById() {
    }
}