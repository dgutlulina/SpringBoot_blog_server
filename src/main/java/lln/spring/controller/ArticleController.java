
package lln.spring.controller;

import lln.spring.entity.Article;
import lln.spring.service.ArticleService;
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
@PostMapping("/publishArticle")
    public String publishArticle(@RequestBody Article article) {
    try {
        articleService.publish(article);
        return "添加成功";
    }catch (Exception e){
        e.printStackTrace();
    }
       return "添加失败";
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

}