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
    
    /**
     * 点赞/取消点赞
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 操作结果
     */
    public Result toggleLike(Integer articleId, Integer userId);
    
    /**
     * 收藏/取消收藏
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 操作结果
     */
    public Result toggleFavorite(Integer articleId, Integer userId);
    
    /**
     * 获取用户发布的文章
     * @param userId 用户ID
     * @param pageParams 分页参数
     * @return 用户发布的文章列表
     */
    public Result getUserArticles(Integer userId, PageParams pageParams);
    
    /**
     * 获取用户点赞的文章
     * @param userId 用户ID
     * @param pageParams 分页参数
     * @return 用户点赞的文章列表
     */
    public Result getUserLikedArticles(Integer userId, PageParams pageParams);
    
    /**
     * 获取用户收藏的文章
     * @param userId 用户ID
     * @param pageParams 分页参数
     * @return 用户收藏的文章列表
     */
    public Result getUserFavoritedArticles(Integer userId, PageParams pageParams);
    
    /**
     * 检查文章是否被用户点赞
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 是否已点赞
     */
    public boolean isArticleLikedByUser(Integer userId, Integer articleId);
    
    /**
     * 检查文章是否被用户收藏
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 是否已收藏
     */
    public boolean isArticleFavoritedByUser(Integer userId, Integer articleId);
    
    /**
     * 点赞文章
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 操作结果
     */
    public boolean likeArticle(Integer userId, Integer articleId);
    
    /**
     * 取消点赞文章
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 操作结果
     */
    public boolean unlikeArticle(Integer userId, Integer articleId);
    
    /**
     * 收藏文章
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 操作结果
     */
    public boolean favoriteArticle(Integer userId, Integer articleId);
    
    /**
     * 取消收藏文章
     * @param userId 用户ID
     * @param articleId 文章ID
     * @return 操作结果
     */
    public boolean unfavoriteArticle(Integer userId, Integer articleId);

}
