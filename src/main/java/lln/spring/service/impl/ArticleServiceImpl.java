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
        QueryWrapper<ArticleVO> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, false, "id");

        wrapper.like(articleSearch.getArticleCondition().getTitle()!="", "title",
                articleSearch.getArticleCondition().getTitle());
        wrapper.ge(articleSearch.getArticleCondition().getStartDate()!=null, "created",
                articleSearch.getArticleCondition().getStartDate());
        wrapper.le(articleSearch.getArticleCondition().getEndDate()!=null, "created",
                articleSearch.getArticleCondition().getEndDate());

        Page<ArticleVO> page = new Page<>(articleSearch.getPageParams().getPage(),
                articleSearch.getPageParams().getRows());
        IPage<ArticleVO> aPage = articleMapper.articleSearch(page, wrapper);
        
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
        result.getMap().put("articleVOs", articleVOs);
        result.getMap().put("pageParams",articleSearch.getPageParams());
        return result;
    }

    @Override
    public Result getAllCategories() {
        Result result = new Result();
        try {
            List<Article> articles = articleMapper.selectList(null);
            Set<String> categoriesSet = new HashSet<>();
            for (Article article : articles) {
                if (article.getCategories() != null && !article.getCategories().isEmpty()) {
                    String[] categories = article.getCategories().split(",");
                    for (String category : categories) {
                        if (!category.trim().isEmpty()) {
                            categoriesSet.add(category.trim());
                        }
                    }
                }
            }
            List<String> categories = new ArrayList<>(categoriesSet);
            result.getMap().put("categories", categories);
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
            List<Article> articles = articleMapper.selectList(null);
            Set<String> tagsSet = new HashSet<>();
            for (Article article : articles) {
                if (article.getTags() != null && !article.getTags().isEmpty()) {
                    String[] tags = article.getTags().split(",");
                    for (String tag : tags) {
                        if (!tag.trim().isEmpty()) {
                            tagsSet.add(tag.trim());
                        }
                    }
                }
            }
            List<String> tags = new ArrayList<>(tagsSet);
            result.getMap().put("tags", tags);
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
            // 这里只是一个占位，因为分类是存储在文章的categories字段中的，没有单独的表
            // 实际应用中可能需要创建一个新的分类表
            result.setMsg("分类添加成功！");
        } catch (Exception e) {
            result.setErrorMessage("添加分类失败！");
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
            // 这里只是一个占位，因为标签是存储在文章的tags字段中的，没有单独的表
            // 实际应用中可能需要创建一个新的标签表
            result.setMsg("标签添加成功！");
        } catch (Exception e) {
            result.setErrorMessage("添加标签失败！");
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
            // 这里需要更新所有使用该分类的文章，将分类从categories字段中移除
            List<Article> articles = articleMapper.selectList(null);
            for (Article article : articles) {
                if (article.getCategories() != null && article.getCategories().contains(category)) {
                    String[] categories = article.getCategories().split(",");
                    List<String> newCategories = new ArrayList<>();
                    for (String cat : categories) {
                        if (!cat.trim().equals(category.trim())) {
                            newCategories.add(cat.trim());
                        }
                    }
                    article.setCategories(String.join(",", newCategories));
                    articleMapper.updateById(article);
                }
            }
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
            // 这里需要更新所有使用该标签的文章，将标签从tags字段中移除
            List<Article> articles = articleMapper.selectList(null);
            for (Article article : articles) {
                if (article.getTags() != null && article.getTags().contains(tag)) {
                    String[] tags = article.getTags().split(",");
                    List<String> newTags = new ArrayList<>();
                    for (String t : tags) {
                        if (!t.trim().equals(tag.trim())) {
                            newTags.add(t.trim());
                        }
                    }
                    article.setTags(String.join(",", newTags));
                    articleMapper.updateById(article);
                }
            }
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
    public Result getFavoriteArticlesByUserId(Integer userId, PageParams pageParams) {
        Result result = new Result();
        try {
            // 获取用户收藏的帖子ID列表
            List<Integer> favoritePostIds = favoriteMapper.findFavoritePostIdsByUserId(userId);
            
            if (favoritePostIds == null || favoritePostIds.isEmpty()) {
                // 没有收藏的帖子
                result.getMap().put("articleVOs", new ArrayList<>());
                result.getMap().put("pageParams", pageParams);
                return result;
            }
            
            // 查询用户收藏的帖子列表，带分页
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
        } catch (Exception e) {
            result.setErrorMessage("获取收藏帖子列表失败");
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
            wrapper.eq("user_id", userId); // 假设Article实体中有user_id字段
            wrapper.orderByDesc("created");
            
            IPage<Article> aPage = articleMapper.selectPage(page, wrapper);
            List<Article> articles = aPage.getRecords();
            
            // 获取当前用户ID
            Integer currentUserId = getCurrentUserId();
            
            // 填充点赞和收藏状态
            if (currentUserId != null && articles != null && !articles.isEmpty()) {
                for (Article article : articles) {
                    // 检查是否点赞
                    Like like = likeMapper.findByPostIdAndUserId(article.getId(), currentUserId);
                    article.setLiked(like != null);
                    
                    // 检查是否收藏
                    Favorite favorite = favoriteMapper.findByPostIdAndUserId(article.getId(), currentUserId);
                    article.setFavorited(favorite != null);
                }
            }
            
            pageParams.setTotal(aPage.getTotal());
            result.getMap().put("articles", articles);
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
            // 获取用户点赞的帖子ID列表
            List<Integer> likedPostIds = likeMapper.findLikedPostIdsByUserId(userId);
            
            if (likedPostIds == null || likedPostIds.isEmpty()) {
                // 没有点赞的帖子
                result.getMap().put("articleVOs", new ArrayList<>());
                result.getMap().put("pageParams", pageParams);
                return result;
            }
            
            // 查询用户点赞的帖子列表，带分页
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
            // 获取用户收藏的帖子ID列表
            List<Integer> favoritePostIds = favoriteMapper.findFavoritePostIdsByUserId(userId);
            
            if (favoritePostIds == null || favoritePostIds.isEmpty()) {
                // 没有收藏的帖子
                result.getMap().put("articleVOs", new ArrayList<>());
                result.getMap().put("pageParams", pageParams);
                return result;
            }
            
            // 查询用户收藏的帖子列表，带分页
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
