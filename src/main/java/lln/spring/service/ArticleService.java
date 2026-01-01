package lln.spring.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lln.spring.entity.Article;
import lln.spring.entity.vo.ArticleVO;
import lln.spring.tools.ArticleSearch;
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

    Result getAPageOfArticleVO(PageParams pageParams,String type);

    public void update(Article article);

//    根据ID查询
public Article selectById(Integer id);

    public void deleteById(Integer id);

    public Result getIndexData();

    public Result getAPageOfArticle(PageParams pageParams);

    public Result getIndexData(PageParams pageParams);

    public Result articleSearch(ArticleSearch articleSearch);

    /**
     * 获取所有分类
     * @return 分类列表
     */
    public Result getAllCategories();

    /**
     * 获取所有标签
     * @return 标签列表
     */
    public Result getAllTags();

    /**
     * 添加或更新分类
     * @param category 分类名称
     * @return 操作结果
     */
    public Result addOrUpdateCategory(String category);

    /**
     * 添加或更新标签
     * @param tag 标签名称
     * @return 操作结果
     */
    public Result addOrUpdateTag(String tag);

    /**
     * 删除分类
     * @param category 分类名称
     * @return 操作结果
     */
    public Result deleteCategory(String category);

    /**
     * 删除标签
     * @param tag 标签名称
     * @return 操作结果
     */
    public Result deleteTag(String tag);

}
