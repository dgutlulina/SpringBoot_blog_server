package lln.spring.controller;

import lln.spring.entity.Comment;
import lln.spring.service.ICommentService;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import lln.spring.tools.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private ICommentService commentService;
    @PostMapping("/insert")
    public Result insert(@RequestBody Comment comment){
        Result result=new Result();
        try{
            comment.setAuthor("李四");
            comment.setCreated(Tools.dateToLocalDate(new Date()));
            Comment comment1=commentService.insert(comment);
            result.getMap().put("comment",comment1);
            result.setMsg("添加评论成功！");
        }catch (Exception e){
            result.setErrorMessage("查询评论失败！");
            e.printStackTrace();
        }
        return result;
    }
    @PostMapping("/getAPageCommentByArticleId")
    public Result getAPageCommentByArticleId(Integer articleId, @RequestBody PageParams pageParams){
        Result result=new Result();
        try{
            result=commentService.getAPageCommentByArticleId(articleId,pageParams);
        }catch (Exception e){
            result.setErrorMessage("查询评论失败！");
            e.printStackTrace();
        }
        return result;
    }
}
