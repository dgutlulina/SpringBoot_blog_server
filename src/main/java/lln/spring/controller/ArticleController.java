package lln.spring.controller;

import lln.spring.entity.Article;
import lln.spring.service.ArticleService;
import lln.spring.entity.User;
import lln.spring.service.IUserService;
import lln.spring.tools.ArticleSearch;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/article")
public class ArticleController {

//    @PostMapping("/postHello")
//    public String postHello() {
//        return "Hello, " ;}
//    @GetMapping("/getHello")
//    public int[] hello1() {
//        return new  int[]{10,20,30};
//    }
@GetMapping("/getHello")
public String getHello() {
    return "hello";
}

@Autowired
    private ArticleService articleService;
    
    @Autowired
    private IUserService userService;

@PostMapping("/getIndexData")
public Result getIndexData(){
    Result result = new Result();
    try{
        result = articleService.getIndexData();
    }catch (Exception e){
        result.setErrorMessage("查询文章失败！");
        e.printStackTrace();
    }
    return result;
}

    @PostMapping("/getAPageOfArticleVO")
    public Result getAPageOfArticleVO(@RequestBody PageParams pageParams) {
        Result result = new Result();
        try {
            result = articleService.getAPageOfArticleVO(pageParams,"id");
        } catch (Exception e) {
            result.setErrorMessage("查询文章失败！");
            e.printStackTrace();
        }
        return result;
    }
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
        User user = userService.getUserInfoByUsername(username); // 使用IUserService接口的正确方法
        return user != null ? user.getId() : null;
    }
    
    @RequestMapping("/publishArticle")
    public String publishArticle(String type, @RequestBody Article article){
        try{
            // 获取当前登录用户ID并设置为文章作者
            Integer userId = getCurrentUserId();
            if (userId != null) {
                article.setAuthorId(userId); // 设置作者ID
            }
            
            if(article.getThumbnail()==null || !article.getThumbnail().contains("/api")){
                article.setThumbnail("/api/images/6.png");//设置默认的文章标题图片
            }
            if("add".equals(type))
                articleService.publish(article);
            else if("edit".equals(type))
                articleService.update(article);
            return "添加成功！";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "添加失败！";
    }
//上传图片

@PostMapping("/upload")
public Result upload(MultipartFile file) {
    Result result = new Result();
    try {
        String url = articleService.upload(file);
        String fullUrl = "http://localhost:8080" + url; // 根据实际后端端口调整
        result.getMap().put("url", fullUrl);
    } catch (Exception e) {
        result.setError("上传失败");
        e.printStackTrace();
    }
    return result;
}

    @PostMapping("/getArticleAndCommentByArticleId")
    public Result getArticleCommentByArticleId(Integer articleId) {
        Result result = new Result();
        try {
            result = articleService.getArticleAndCommentByArticleId(articleId);
        } catch (Exception e) {
            result.setErrorMessage("查询文章失败！");
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/getArticleAndFirstPageCommentByArticleId")
    public Result getArticleAndFirstPageCommentByArticleId(Integer articleId,@RequestBody PageParams pageParams) {
        Result result = new Result();
        try {
            result = articleService.getArticleAndFirstPageCommentByArticleId(articleId, pageParams);
        } catch (Exception e) {
            result.setErrorMessage("查询文章失败！");
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("/getArticleById")
    public Result getArticleById(Integer id){
        Result result=new Result();
        try{
            Article article=articleService.selectById(id);
            result.getMap().put("article",article);
        }catch (Exception e){
            result.setErrorMessage("查询失败！");
            e.printStackTrace();
        }
        return result;
    }

//    根据ID删除
    @PostMapping("/deleteById")
    public Result deleteById(Integer id){
        Result result=new Result();
        try{
            articleService.deleteById(id);
        }catch (Exception e){
            result.setErrorMessage("删除失败！");
            e.printStackTrace();
        }
        return result;
    }
    @PostMapping("/getIndexData1")
    public Result getIndexData1(@RequestBody PageParams pageParams){
        Result result=new Result();
        try{
            result=articleService.getIndexData(pageParams);
        }catch (Exception e){
            result.setErrorMessage("获取数据失败！");
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/getAPageOfArticle")
    public Result getAPageOfArticle(@RequestBody PageParams pageParams){
        Result result=new Result();
        try{
            result=articleService.getAPageOfArticle(pageParams);
        }catch (Exception e){
            result.setErrorMessage("查询文章失败！");
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/articleSearch")
    public Result articleSearch(@RequestBody ArticleSearch articleSearch) {
        Result result = new Result();
        try {
            result = articleService.articleSearch(articleSearch);
        } catch (Exception e) {
            result.setErrorMessage("获取数据失败！");
            e.printStackTrace();
        }
        return result;
    }

    // 分类管理接口
    @PostMapping("/getAllCategories")
    public Result getAllCategories() {
        Result result = new Result();
        try {
            result = articleService.getAllCategories();
        } catch (Exception e) {
            result.setErrorMessage("获取分类失败！");
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/addOrUpdateCategory")
    public Result addOrUpdateCategory(String category) {
        Result result = new Result();
        try {
            result = articleService.addOrUpdateCategory(category);
        } catch (Exception e) {
            result.setErrorMessage("操作分类失败！");
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/deleteCategory")
    public Result deleteCategory(String category) {
        Result result = new Result();
        try {
            result = articleService.deleteCategory(category);
        } catch (Exception e) {
            result.setErrorMessage("删除分类失败！");
            e.printStackTrace();
        }
        return result;
    }

    // 标签管理接口
    @PostMapping("/getAllTags")
    public Result getAllTags() {
        Result result = new Result();
        try {
            result = articleService.getAllTags();
        } catch (Exception e) {
            result.setErrorMessage("获取标签失败！");
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/addOrUpdateTag")
    public Result addOrUpdateTag(String tag) {
        Result result = new Result();
        try {
            result = articleService.addOrUpdateTag(tag);
        } catch (Exception e) {
            result.setErrorMessage("操作标签失败！");
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/deleteTag")
    public Result deleteTag(String tag) {
        Result result = new Result();
        try {
            result = articleService.deleteTag(tag);
        } catch (Exception e) {
            result.setErrorMessage("删除标签失败！");
            e.printStackTrace();
        }
        return result;
    }
    
    // 用户帖子管理相关API
    
    @PostMapping("/getUserArticles")
    public Result getUserArticles(Integer userId, @RequestBody PageParams pageParams) {
        // 检查权限，只允许用户查看自己的帖子或管理员查看所有用户帖子
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        // 如果不是当前用户也不是管理员，则拒绝访问
        if (!currentUser.getId().equals(userId) && !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_admin"))) {
            return Result.error("权限不足，无法查看其他用户的帖子");
        }
        
        Result result = new Result();
        try {
            result = articleService.getUserArticles(userId, pageParams);
        } catch (Exception e) {
            result.setErrorMessage("获取用户帖子失败！");
            e.printStackTrace();
        }
        return result;
    }
    
    @PostMapping("/getUserLikedArticles")
    public Result getUserLikedArticles(Integer userId, @RequestBody PageParams pageParams) {
        // 检查权限，只允许用户查看自己的点赞帖子或管理员查看所有用户点赞
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        // 如果不是当前用户也不是管理员，则拒绝访问
        if (!currentUser.getId().equals(userId) && !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_admin"))) {
            return Result.error("权限不足，无法查看其他用户的点赞帖子");
        }
        
        Result result = new Result();
        try {
            result = articleService.getUserLikedArticles(userId, pageParams);
        } catch (Exception e) {
            result.setErrorMessage("获取用户点赞帖子失败！");
            e.printStackTrace();
        }
        return result;
    }
    
    @PostMapping("/getUserFavoritedArticles")
    public Result getUserFavoritedArticles(Integer userId, @RequestBody PageParams pageParams) {
        // 检查权限，只允许用户查看自己的收藏帖子或管理员查看所有用户收藏
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        // 如果不是当前用户也不是管理员，则拒绝访问
        if (!currentUser.getId().equals(userId) && !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_admin"))) {
            return Result.error("权限不足，无法查看其他用户的收藏帖子");
        }
        
        Result result = new Result();
        try {
            result = articleService.getUserFavoritedArticles(userId, pageParams);
        } catch (Exception e) {
            result.setErrorMessage("获取用户收藏帖子失败！");
            e.printStackTrace();
        }
        return result;
    }
    
    @GetMapping("/isArticleLikedByUser")
    public Result isArticleLikedByUser(Integer userId, Integer articleId) {
        try {
            boolean isLiked = articleService.isArticleLikedByUser(userId, articleId);
            Result result = new Result();
            result.setSuccess(true);
            result.setMsg(isLiked ? "已点赞" : "未点赞");
            result.setData(isLiked); // 添加这行来返回实际的点赞状态
            return result;
        } catch (Exception e) {
            Result result = new Result();
            result.setErrorMessage("检查点赞状态失败！");
            e.printStackTrace();
            return result;
        }
    }
    
    @GetMapping("/isArticleFavoritedByUser")
    public Result isArticleFavoritedByUser(Integer userId, Integer articleId) {
        try {
            boolean isFavorited = articleService.isArticleFavoritedByUser(userId, articleId);
            Result result = new Result();
            result.setSuccess(true);
            result.setMsg(isFavorited ? "已收藏" : "未收藏");
            result.setData(isFavorited); // 添加这行来返回实际的收藏状态
            return result;
        } catch (Exception e) {
            Result result = new Result();
            result.setErrorMessage("检查收藏状态失败！");
            e.printStackTrace();
            return result;
        }
    }
    
    @PostMapping("/likeArticle")
    public Result likeArticle(@RequestBody Map<String, Integer> request) {
        Integer articleId = request.get("articleId");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        try {
            boolean success = articleService.likeArticle(currentUser.getId(), articleId);
            if (success) {
                Result result = new Result();
                result.setSuccess(true);
                result.setMsg("点赞成功");
                return result;
            } else {
                Result result = new Result();
                result.setSuccess(false);
                result.setMsg("点赞失败或已点赞");
                return result;
            }
        } catch (Exception e) {
            Result result = new Result();
            result.setErrorMessage("点赞失败！");
            e.printStackTrace();
            return result;
        }
    }
    
    @PostMapping("/unlikeArticle")
    public Result unlikeArticle(@RequestBody Map<String, Integer> request) {
        Integer articleId = request.get("articleId");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        try {
            boolean success = articleService.unlikeArticle(currentUser.getId(), articleId);
            if (success) {
                Result result = new Result();
                result.setSuccess(true);
                result.setMsg("取消点赞成功");
                return result;
            } else {
                Result result = new Result();
                result.setSuccess(false);
                result.setMsg("取消点赞失败");
                return result;
            }
        } catch (Exception e) {
            Result result = new Result();
            result.setErrorMessage("取消点赞失败！");
            e.printStackTrace();
            return result;
        }
    }
    
    @PostMapping("/favoriteArticle")
    public Result favoriteArticle(@RequestBody Map<String, Integer> request) {
        Integer articleId = request.get("articleId");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        try {
            boolean success = articleService.favoriteArticle(currentUser.getId(), articleId);
            if (success) {
                Result result = new Result();
                result.setSuccess(true);
                result.setMsg("收藏成功");
                return result;
            } else {
                Result result = new Result();
                result.setSuccess(false);
                result.setMsg("收藏失败或已收藏");
                return result;
            }
        } catch (Exception e) {
            Result result = new Result();
            result.setErrorMessage("收藏失败！");
            e.printStackTrace();
            return result;
        }
    }
    
    @PostMapping("/unfavoriteArticle")
    public Result unfavoriteArticle(@RequestBody Map<String, Integer> request) {
        Integer articleId = request.get("articleId");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        try {
            boolean success = articleService.unfavoriteArticle(currentUser.getId(), articleId);
            if (success) {
                Result result = new Result();
                result.setSuccess(true);
                result.setMsg("取消收藏成功");
                return result;
            } else {
                Result result = new Result();
                result.setSuccess(false);
                result.setMsg("取消收藏失败");
                return result;
            }
        } catch (Exception e) {
            Result result = new Result();
            result.setErrorMessage("取消收藏失败！");
            e.printStackTrace();
            return result;
        }
    }

}