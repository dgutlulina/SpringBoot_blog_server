package lln.spring.service;

import lln.spring.entity.Article;
import lln.spring.tools.Result;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {
    //发布文章
    public void publish(Article article);

    //上传图片
    public String upload(MultipartFile file);

    //获取文章和评论
    public Result getArticleAndCommentByArticleId (Integer articleId);
}
