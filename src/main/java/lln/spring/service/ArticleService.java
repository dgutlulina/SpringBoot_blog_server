package lln.spring.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lln.spring.entity.Article;
import lln.spring.entity.vo.ArticleVO;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {
    //发布文章
    public void publish(Article article);

    //上传图片
    public String upload(MultipartFile file);

    public Result getArticleAndFirstPageCommentByArticleId(Integer articleId, PageParams pageParams);

    //获取文章和评论
    public Result getArticleAndCommentByArticleId (Integer articleId);

    Result getAPageOfArticleVO(PageParams pageParams);

    public void update(Article article);

//    根据ID查询
public Article selectById(Integer id);

    public void deleteById(Integer id);


}
