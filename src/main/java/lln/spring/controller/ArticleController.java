package lln.spring.controller;

import lln.spring.entity.Article;
import lln.spring.service.ArticleService;
import lln.spring.tools.ArticleSearch;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @RequestMapping("/publishArticle")
    public String publishArticle(String type, @RequestBody Article article){
        try{
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

}