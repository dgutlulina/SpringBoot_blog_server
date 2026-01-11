package lln.spring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lln.spring.entity.Statistic;
import lln.spring.entity.vo.ArticleVO;
import lln.spring.mapper.ArticleMapper;
import lln.spring.entity.Article;
import lln.spring.entity.Like;
import lln.spring.entity.Favorite;
import lln.spring.mapper.CommentMapper;
import lln.spring.mapper.StatisticMapper;
import lln.spring.mapper.LikeMapper;
import lln.spring.mapper.FavoriteMapper;
import lln.spring.mapper.UserMapper;
import lln.spring.entity.User;
import lln.spring.service.ArticleService;
import lln.spring.tools.ArticleSearch;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import com.baomidou.mybatisplus.core.conditions.Wrapper;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lln.spring.entity.Category;
import lln.spring.entity.Tag;
import lln.spring.mapper.CategoryMapper;
import lln.spring.mapper.TagMapper;

@Service // 确保该注解存在，让Spring扫描为Bean
public class ArticleServiceImpl implements ArticleService {
    @Value("${uploadImagesDir}")
    private String uploadImagesDir;//

    @Autowired
    private ArticleMapper articleMapper;
    
    @Autowired
    private LikeMapper likeMapper;
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private StatisticMapper statisticMapper;

    // 修正方法名，添加@Override注解，与接口方法一致
//    @Override
//    public void publish(Article article) {
//        articleMapper.insert(article);
//    }

    @Override
    public Result getIndexData(){
        Result result = new Result();
        List< Article> articles = articleMapper.getPage(0,5);
        result.getMap().put("articles",articles);

        PageParams pageParams = new PageParams();
        pageParams.setPage(1L);
        pageParams.setRows(10L);
        Result result1 = getAPageOfArticleVO(pageParams,"hits");
        result.getMap().put("articleVOs", result1.getMap().get("articleVOs"));

        return result;
    }


    @SneakyThrows
    @Override
    public String upload(MultipartFile file){
        final File folder = new File(uploadImagesDir);
        if(!folder.exists()){
            folder.mkdirs();
        }
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        final  String newName = UUID.randomUUID() + type;
        file.transferTo(new File(folder,newName));
        String url = "/api/images/"+newName;
        return url;
    }
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取当前登录用户的ID
     * @return 用户ID
     */
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        String username = authentication.getName();
        User user = userMapper.findByName(username);
        return user != null ? user.getId() : null;
    }

    @Override
    public Result getAPageOfArticleVO(PageParams pageParams,String type) {
        QueryWrapper<ArticleVO> queryWrapper = new QueryWrapper<>();

        if("id".equals( type))
            queryWrapper.orderBy(true, false, "t_article.id");
        else
            queryWrapper.orderBy(true, false, "t_statistic.hits");

        queryWrapper.orderBy(true, false, "t_article.id");
        queryWrapper.apply("t_article.id = t_statistic.article_id");
        String s = queryWrapper.getCustomSqlSegment();
        Page<ArticleVO> page = new Page<>(pageParams.getPage(), pageParams.getRows());
        Wrapper wrapper = queryWrapper;
        IPage<ArticleVO> aPage = articleMapper.getAPageOfArticleVO(page, wrapper);
        
        // 获取当前用户ID
        Integer userId = getCurrentUserId();
        List<ArticleVO> articleVOs = aPage.getRecords();
        
        // 填充点赞和收藏状态
        if (userId != null && articleVOs != null && !articleVOs.isEmpty()) {
            for (ArticleVO articleVO : articleVOs) {
                // 检查是否点赞
                Like like = likeMapper.findByPostIdAndUserId(articleVO.getId(), userId);
                articleVO.setLiked(like != null);
                
                // 检查是否收藏
                Favorite favorite = favoriteMapper.findByPostIdAndUserId(articleVO.getId(), userId);
                articleVO.setFavorited(favorite != null);
            }
        }
        
        Result result = new Result();
        pageParams.setTotal(aPage.getTotal());
        result.getMap().put("articleVOs", articleVOs);
        result.getMap().put("pageParams", pageParams);
        return result;
    }

    @Override
    public Result getArticleAndCommentByArticleId(Integer articleId) {
        Result result = new Result();
        Article article = articleMapper.selectById(articleId);
        result.getMap().put("article", article);
        result.getMap().put("comments", commentMapper.selectByArticleId(articleId));
        
        // 添加点赞和收藏状态
        Integer userId = getCurrentUserId();
        if (userId != null) {
            Like like = likeMapper.findByPostIdAndUserId(articleId, userId);
            Favorite favorite = favoriteMapper.findByPostIdAndUserId(articleId, userId);
            result.getMap().put("isLiked", like != null);
            result.getMap().put("isFavorited", favorite != null);
        } else {
            result.getMap().put("isLiked", false);
            result.getMap().put("isFavorited", false);
        }
        
        // 添加点赞数
        Integer heartCount = likeMapper.countByPostId(articleId);
        result.getMap().put("heartCount", heartCount != null ? heartCount : 0);
        
        return result;
    }

    @Override
    public Result getArticleAndFirstPageCommentByArticleId(Integer articleId, PageParams pageParams){
        Result result=new Result();
        Article article=articleMapper.selectById(articleId);
        result.getMap().put("article",article);
        // 对page和rows进行类型转换，将Long转为Integer
        Long offsetLong = (pageParams.getPage() - 1) * pageParams.getRows();
        Integer offset = offsetLong.intValue();
        Integer rows = pageParams.getRows().intValue();
        result.getMap().put("comments",
                commentMapper.getAPageCommentByArticleId(
                        articleId, offset, rows));

        // 文章的点击次数+1
        Statistic statistic=statisticMapper.selectByArticleId(articleId);
        if (statistic != null) {
            statistic.setHits(statistic.getHits()+1);
            statisticMapper.updateById(statistic);
        }
        
        // 添加点赞和收藏状态
        Integer userId = getCurrentUserId();
        if (userId != null) {
            Like like = likeMapper.findByPostIdAndUserId(articleId, userId);
            Favorite favorite = favoriteMapper.findByPostIdAndUserId(articleId, userId);
            result.getMap().put("isLiked", like != null);
            result.getMap().put("isFavorited", favorite != null);
        } else {
            result.getMap().put("isLiked", false);
            result.getMap().put("isFavorited", false);
        }
        
        // 添加点赞数
        Integer heartCount = likeMapper.countByPostId(articleId);
        result.getMap().put("heartCount", heartCount != null ? heartCount : 0);

        return result;
    }

    //发布文章
    @Override
    @Transactional
    public void publish(Article article){
        article.setCreated(new Date());
        articleMapper.insert(article);

        Statistic statistic=new Statistic();
        statistic.setArticleId(article.getId());
        statistic.setHits(0);
        statistic.setCommentsNum(0);
        statisticMapper.insert(statistic);
    }

    @Override
    public void update(Article article){
        Article newArticle=articleMapper.selectById(article.getId());
        if (newArticle != null) {
            newArticle.setModified(new Date());
            newArticle.setCategories(article.getCategories()); // 添加分类字段的更新
            newArticle.setTags(article.getTags());
            newArticle.setContent(article.getContent());
            newArticle.setTitle(article.getTitle());
            newArticle.setThumbnail(article.getThumbnail());
            articleMapper.updateById(newArticle);
        }
    }

//    根据id查询
    @Override
    public Article selectById(Integer id){
    return articleMapper.selectById(id);
}
//根据id删除
    @Override
    public void deleteById(Integer id){
    articleMapper.deleteById(id);
}

    @Override
    public Result getAPageOfArticle(PageParams pageParams) {
        QueryWrapper<ArticleVO> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, false, "t_article.id");

        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getRows());
        IPage<Article> aPage = articleMapper.getAPageOfArticle(page, wrapper);
        Result result = new Result();
        pageParams.setTotal(aPage.getTotal());

        //只返回部分文章内容
        if(aPage.getRecords()!=null && aPage.getRecords().size()>0){
            for(Article article:aPage.getRecords()){
                Document doc = Jsoup.parse(article.getContent());
                String content=doc.text();
                if(content.length()>100)
                    content=content.substring(0,99)+"......";
                article.setContent(content);
            }
        }

        result.getMap().put("articles", aPage.getRecords());
        result.getMap().put("pageParams", pageParams);
        return result;
    }
    @Override
    public Result getIndexData(PageParams pageParams){
        //查文章分页
        Result result=getAPageOfArticle(pageParams);
        //查点击量排名前十文章
        PageParams pageParams1 = new PageParams();
        pageParams1.setPage(1L);
        pageParams1.setRows(10L);
        Result result1 = getAPageOfArticleVO(pageParams1, "hits");
        result.getMap().put("articleVOs", result1.getMap().get("articleVOs"));
        return result;
    }

    @Override
    public Result articleSearch(ArticleSearch articleSearch){
        // 检查参数是否为空
        if (articleSearch == null || articleSearch.getArticleCondition() == null || articleSearch.getPageParams() == null) {
            Result result = new Result();
            result.setErrorMessage("参数错误");
            return result;
        }
        
        // 使用Article表的别名来避免冲突
        QueryWrapper<ArticleVO> wrapper = new QueryWrapper<>();
        wrapper.apply("t_article.id = t_statistic.article_id"); // 确保连接条件
        wrapper.orderByDesc("t_article.id"); // 使用表别名

        // 安全地获取标题并进行搜索，处理可能的空值
        String title = articleSearch.getArticleCondition().getTitle();
        if (title != null && !title.trim().isEmpty()) {
            wrapper.like("t_article.title", title.trim());
        }
        
        // 安全地处理日期范围
        if (articleSearch.getArticleCondition().getStartDate() != null) {
            wrapper.ge("t_article.created", articleSearch.getArticleCondition().getStartDate());
        }
        if (articleSearch.getArticleCondition().getEndDate() != null) {
            wrapper.le("t_article.created", articleSearch.getArticleCondition().getEndDate());
        }

        Page<ArticleVO> page = new Page<>(articleSearch.getPageParams().getPage(),
                articleSearch.getPageParams().getRows());
        IPage<ArticleVO> aPage;
        try {
            aPage = articleMapper.articleSearch(page, wrapper);
        } catch (Exception e) {
            Result result = new Result();
            result.setErrorMessage("搜索文章时发生错误: " + e.getMessage());
            e.printStackTrace();
            return result;
        }
        
        // 获取当前用户ID
        Integer userId = getCurrentUserId();
        List<ArticleVO> articleVOs = aPage.getRecords();
        
        // 填充点赞和收藏状态
        if (userId != null && articleVOs != null && !articleVOs.isEmpty()) {
            for (ArticleVO articleVO : articleVOs) {
                // 检查是否点赞
                Like like = likeMapper.findByPostIdAndUserId(articleVO.getId(), userId);
                articleVO.setLiked(like != null);
                
                // 检查是否收藏
                Favorite favorite = favoriteMapper.findByPostIdAndUserId(articleVO.getId(), userId);
                articleVO.setFavorited(favorite != null);
            }
        }
        
        Result result=new Result();
        articleSearch.getPageParams().setTotal(aPage.getTotal());
        result.getMap().put("articleVOs", articleVOs != null ? articleVOs : new ArrayList<>());
        result.getMap().put("pageParams", articleSearch.getPageParams());
        result.setMsg("搜索文章成功");
        return result;
    }

    @Override
    public Result getAllCategories() {
        Result result = new Result();
        try {
            // 从分类表中获取所有分类
            List<Category> categories = categoryMapper.selectList(null);
            List<String> categoryNames = new ArrayList<>();
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }
            result.getMap().put("categories", categoryNames);
            result.setMsg("获取分类列表成功！");
        } catch (Exception e) {
            result.setErrorMessage("获取分类列表失败！");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result getAllTags() {
        Result result = new Result();
        try {
            // 从标签表中获取所有标签
            List<Tag> tags = tagMapper.selectList(null);
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : tags) {
                tagNames.add(tag.getName());
            }
            result.getMap().put("tags", tagNames);
            result.setMsg("获取标签列表成功！");
        } catch (Exception e) {
            result.setErrorMessage("获取标签列表失败！");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result addOrUpdateCategory(String category) {
        Result result = new Result();
        try {
            if (category == null || category.trim().isEmpty()) {
                result.setErrorMessage("分类名称不能为空！");
                return result;
            }
            
            // 检查分类是否已存在
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Category> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("name", category.trim());
            Category existingCategory = categoryMapper.selectOne(queryWrapper);
            
            if (existingCategory == null) {
                // 分类不存在，创建新分类
                Category newCategory = new Category();
                newCategory.setName(category.trim());
                newCategory.setCreated(new Date());
                categoryMapper.insert(newCategory);
            } else {
                // 分类已存在，更新时间
                existingCategory.setCreated(new Date());
                categoryMapper.updateById(existingCategory);
            }
            
            result.setMsg("分类操作成功！");
        } catch (Exception e) {
            result.setErrorMessage("操作分类失败！");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result addOrUpdateTag(String tag) {
        Result result = new Result();
        try {
            if (tag == null || tag.trim().isEmpty()) {
                result.setErrorMessage("标签名称不能为空！");
                return result;
            }
            
            // 检查标签是否已存在
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Tag> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("name", tag.trim());
            Tag existingTag = tagMapper.selectOne(queryWrapper);
            
            if (existingTag == null) {
                // 标签不存在，创建新标签
                Tag newTag = new Tag();
                newTag.setName(tag.trim());
                newTag.setCreated(new Date());
                tagMapper.insert(newTag);
            } else {
                // 标签已存在，更新时间
                existingTag.setCreated(new Date());
                tagMapper.updateById(existingTag);
            }
            
            result.setMsg("标签操作成功！");
        } catch (Exception e) {
            result.setErrorMessage("操作标签失败！");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result deleteCategory(String category) {
        Result result = new Result();
        try {
            if (category == null || category.trim().isEmpty()) {
                result.setErrorMessage("分类名称不能为空！");
                return result;
            }
            
            // 从分类表中删除分类
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Category> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("name", category.trim());
            categoryMapper.delete(queryWrapper);
            
            result.setMsg("分类删除成功！");
        } catch (Exception e) {
            result.setErrorMessage("删除分类失败！");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result deleteTag(String tag) {
        Result result = new Result();
        try {
            if (tag == null || tag.trim().isEmpty()) {
                result.setErrorMessage("标签名称不能为空！");
                return result;
            }
            
            // 从标签表中删除标签
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Tag> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("name", tag.trim());
            tagMapper.delete(queryWrapper);
            
            result.setMsg("标签删除成功！");
        } catch (Exception e) {
            result.setErrorMessage("删除标签失败！");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    @Transactional
    public Result toggleLike(Integer articleId, Integer userId) {
        Result result = new Result();
        try {
            // 检查是否已点赞
            Like like = likeMapper.findByPostIdAndUserId(articleId, userId);
            Statistic statistic = statisticMapper.selectOne(new QueryWrapper<Statistic>().eq("article_id", articleId));
            
            if (like == null) {
                // 未点赞，添加点赞
                like = new Like();
                like.setArticleId(articleId);
                like.setUserId(userId);
                like.setCreatedAt(LocalDateTime.now());
                likeMapper.insert(like);
                
                // 点赞数由 likeMapper.countByArticleId() 动态计算，无需在这里更新
                
                result.setMsg("点赞成功");
                result.setData(true); // 返回已点赞状态
            } else {
                // 已点赞，取消点赞
                likeMapper.deleteById(like.getId());
                
                // 点赞数由 likeMapper.countByArticleId() 动态计算，无需在这里更新
                
                result.setMsg("取消点赞成功");
                result.setData(false); // 返回未点赞状态
            }
        } catch (Exception e) {
            result.setErrorMessage("操作失败");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    @Transactional
    public Result toggleFavorite(Integer articleId, Integer userId) {
        Result result = new Result();
        try {
            // 检查是否已收藏
            Favorite favorite = favoriteMapper.findByPostIdAndUserId(articleId, userId);
            
            if (favorite == null) {
                // 未收藏，添加收藏
                favorite = new Favorite();
                favorite.setArticleId(articleId);
                favorite.setUserId(userId);
                favorite.setCreatedAt(LocalDateTime.now());
                favoriteMapper.insert(favorite);
                
                result.setMsg("收藏成功");
                result.setData(true); // 返回已收藏状态
            } else {
                // 已收藏，取消收藏
                favoriteMapper.deleteById(favorite.getId());
                
                result.setMsg("取消收藏成功");
                result.setData(false); // 返回未收藏状态
            }
        } catch (Exception e) {
            result.setErrorMessage("操作失败");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result getUserArticles(Integer userId, PageParams pageParams) {
        Result result = new Result();
        try {
            // 查询用户发布的文章列表，带分页
            Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getRows());
            QueryWrapper<Article> wrapper = new QueryWrapper<>();
            wrapper.eq("author_id", userId); // 使用Article实体中的author_id字段
            wrapper.orderByDesc("created");
            
            IPage<Article> aPage = articleMapper.selectPage(page, wrapper);
            List<Article> articles = aPage.getRecords();
            
            // 转换为ArticleVO对象，包含分类和浏览量信息
            List<ArticleVO> articleVOs = new ArrayList<>();
            if (articles != null && !articles.isEmpty()) {
                for (Article article : articles) {
                    ArticleVO articleVO = new ArticleVO();
                    articleVO.setId(article.getId());
                    articleVO.setTitle(article.getTitle());
                    articleVO.setCreated(article.getCreated());
                    articleVO.setCategories(article.getCategories()); // 分类字段
                    articleVO.setThumbnail(article.getThumbnail());
                    
                    // 获取文章统计信息（浏览量）
                    Statistic statistic = statisticMapper.selectByArticleId(article.getId());
                    if (statistic != null) {
                        articleVO.setHits(statistic.getHits()); // 浏览量
                        articleVO.setHeartCount(statistic.getHeartCount()); // 点赞数
                    } else {
                        articleVO.setHits(0);
                        articleVO.setHeartCount(0);
                    }
                    
                    // 获取当前用户ID，检查是否已点赞和收藏
                    Integer currentUserId = getCurrentUserId();
                    if (currentUserId != null) {
                        Like like = likeMapper.findByPostIdAndUserId(article.getId(), currentUserId);
                        articleVO.setLiked(like != null);
                        
                        Favorite favorite = favoriteMapper.findByPostIdAndUserId(article.getId(), currentUserId);
                        articleVO.setFavorited(favorite != null);
                    } else {
                        articleVO.setLiked(false);
                        articleVO.setFavorited(false);
                    }
                    
                    articleVOs.add(articleVO);
                }
            }
            
            pageParams.setTotal(aPage.getTotal());
            result.getMap().put("articles", articleVOs);
            result.getMap().put("pageParams", pageParams);
            result.setMsg("获取用户文章成功");
        } catch (Exception e) {
            result.setErrorMessage("获取用户文章失败");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result getUserLikedArticles(Integer userId, PageParams pageParams) {
        Result result = new Result();
        try {
            // 获取用户点赞的文章ID列表
            List<Integer> likedPostIds = likeMapper.findLikedPostIdsByUserId(userId);
            
            if (likedPostIds == null || likedPostIds.isEmpty()) {
                // 没有点赞的文章
                result.getMap().put("articleVOs", new ArrayList<>());
                result.getMap().put("pageParams", pageParams);
                return result;
            }
            
            // 查询用户点赞的文章列表，带分页
            Page<ArticleVO> page = new Page<>(pageParams.getPage(), pageParams.getRows());
            QueryWrapper<ArticleVO> wrapper = new QueryWrapper<>();
            wrapper.in("t_article.id", likedPostIds);
            wrapper.orderBy(true, false, "t_article.id");
            wrapper.apply("t_article.id = t_statistic.article_id");
            
            IPage<ArticleVO> aPage = articleMapper.getAPageOfArticleVO(page, wrapper);
            List<ArticleVO> articleVOs = aPage.getRecords();
            
            // 设置点赞和收藏状态
            if (articleVOs != null && !articleVOs.isEmpty()) {
                for (ArticleVO articleVO : articleVOs) {
                    articleVO.setLiked(true); // 肯定是点赞的
                    articleVO.setFavorited(favoriteMapper.findByPostIdAndUserId(articleVO.getId(), userId) != null);
                }
            }
            
            pageParams.setTotal(aPage.getTotal());
            result.getMap().put("articleVOs", articleVOs);
            result.getMap().put("pageParams", pageParams);
            result.setMsg("获取用户点赞文章成功");
        } catch (Exception e) {
            result.setErrorMessage("获取用户点赞文章失败");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Result getUserFavoritedArticles(Integer userId, PageParams pageParams) {
        Result result = new Result();
        try {
            // 获取用户收藏的文章ID列表
            List<Integer> favoritePostIds = favoriteMapper.findFavoritePostIdsByUserId(userId);
            
            if (favoritePostIds == null || favoritePostIds.isEmpty()) {
                // 没有收藏的文章
                result.getMap().put("articleVOs", new ArrayList<>());
                result.getMap().put("pageParams", pageParams);
                return result;
            }
            
            // 查询用户收藏的文章列表，带分页
            Page<ArticleVO> page = new Page<>(pageParams.getPage(), pageParams.getRows());
            QueryWrapper<ArticleVO> wrapper = new QueryWrapper<>();
            wrapper.in("t_article.id", favoritePostIds);
            wrapper.orderBy(true, false, "t_article.id");
            wrapper.apply("t_article.id = t_statistic.article_id");
            
            IPage<ArticleVO> aPage = articleMapper.getAPageOfArticleVO(page, wrapper);
            List<ArticleVO> articleVOs = aPage.getRecords();
            
            // 设置点赞和收藏状态
            if (articleVOs != null && !articleVOs.isEmpty()) {
                for (ArticleVO articleVO : articleVOs) {
                    articleVO.setLiked(likeMapper.findByPostIdAndUserId(articleVO.getId(), userId) != null);
                    articleVO.setFavorited(true); // 肯定是收藏的
                }
            }
            
            pageParams.setTotal(aPage.getTotal());
            result.getMap().put("articleVOs", articleVOs);
            result.getMap().put("pageParams", pageParams);
            result.setMsg("获取用户收藏文章成功");
        } catch (Exception e) {
            result.setErrorMessage("获取用户收藏文章失败");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean isArticleLikedByUser(Integer userId, Integer articleId) {
        Like like = likeMapper.findByPostIdAndUserId(articleId, userId);
        return like != null;
    }

    @Override
    public boolean isArticleFavoritedByUser(Integer userId, Integer articleId) {
        Favorite favorite = favoriteMapper.findByPostIdAndUserId(articleId, userId);
        return favorite != null;
    }

    @Override
    public boolean likeArticle(Integer userId, Integer articleId) {
        try {
            // 检查是否已点赞
            Like existingLike = likeMapper.findByPostIdAndUserId(articleId, userId);
            
            if (existingLike != null) {
                // 已点赞，取消点赞
                likeMapper.deleteById(existingLike.getId());
                return false; // 返回取消点赞状态
            } else {
                // 未点赞，添加点赞
                Like like = new Like();
                like.setArticleId(articleId);
                like.setUserId(userId);
                like.setCreatedAt(LocalDateTime.now()); // 使用LocalDateTime
                likeMapper.insert(like);
                return true; // 返回点赞状态
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unlikeArticle(Integer userId, Integer articleId) {
        try {
            // 查找点赞记录
            Like existingLike = likeMapper.findByPostIdAndUserId(articleId, userId);
            
            if (existingLike != null) {
                // 取消点赞
                likeMapper.deleteById(existingLike.getId());
                return true; // 返回取消点赞成功
            } else {
                return false; // 没有点赞记录，取消失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean favoriteArticle(Integer userId, Integer articleId) {
        try {
            // 检查是否已收藏
            Favorite existingFavorite = favoriteMapper.findByPostIdAndUserId(articleId, userId);
            
            if (existingFavorite != null) {
                // 已收藏，取消收藏
                favoriteMapper.deleteById(existingFavorite.getId());
                return false; // 返回取消收藏状态
            } else {
                // 未收藏，添加收藏
                Favorite favorite = new Favorite();
                favorite.setArticleId(articleId);
                favorite.setUserId(userId);
                favorite.setCreatedAt(LocalDateTime.now()); // 使用LocalDateTime
                favoriteMapper.insert(favorite);
                return true; // 返回收藏状态
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unfavoriteArticle(Integer userId, Integer articleId) {
        try {
            // 查找收藏记录
            Favorite existingFavorite = favoriteMapper.findByPostIdAndUserId(articleId, userId);
            
            if (existingFavorite != null) {
                // 取消收藏
                favoriteMapper.deleteById(existingFavorite.getId());
                return true; // 返回取消收藏成功
            } else {
                return false; // 没有收藏记录，取消失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
