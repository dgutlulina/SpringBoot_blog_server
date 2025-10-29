package lln.spring.service.impl;

import lln.spring.mapper.ArticleMapper;
import lln.spring.entity.Article;
import lln.spring.mapper.CommentMapper;
import lln.spring.service.ArticleService;
import lln.spring.tools.Result;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service // 确保该注解存在，让Spring扫描为Bean
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    // 修正方法名，添加@Override注解，与接口方法一致
    @Override
    public void publish(Article article) {
        articleMapper.insert(article);
    }

    @SneakyThrows
    @Override
    public String upload(MultipartFile file){
        final File folder = new File("D:/Springboot");
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

    public Result getArticleAndCommentByArticleId(Integer articleId) {
        Result result = new Result();
        result.getMap().put("article", articleMapper.selectById(articleId));
        result.getMap().put("comments", commentMapper.selectByArticleId(articleId));
        return result;
    }
}
