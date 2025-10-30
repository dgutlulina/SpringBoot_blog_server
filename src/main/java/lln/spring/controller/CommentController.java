package lln.spring.controller;

import lln.spring.entity.Comment;
import lln.spring.service.ICommentService;
import lln.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment){
        Result result=new Result();
        try{
            comment.setAuthor("李四");
            comment.setCreated(LocalDate.now());
            Comment comment1=commentService.addComment(comment);
            result.getMap().put("comment",comment1);
            result.setMsg("添加评论成功！");
        }catch (Exception e){
            result.setErrorMessage("添加评论失败！");
            e.printStackTrace();
        }
        return result;
    }
}
