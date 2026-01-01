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

    /**
     * 获取所有评论的分页列表
     * @param pageParams 分页参数
     * @return 评论列表
     */
    @PostMapping("/getAllComments")
    public Result getAllComments(@RequestBody PageParams pageParams){
        Result result=new Result();
        try{
            result=commentService.getAllComments(pageParams);
        }catch (Exception e){
            result.setErrorMessage("查询评论失败！");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新评论状态
     * @param id 评论ID
     * @param status 新状态
     * @return 更新结果
     */
    @PostMapping("/updateStatus")
    public Result updateCommentStatus(Integer id, String status){
        Result result=new Result();
        try{
            result=commentService.updateCommentStatus(id, status);
        }catch (Exception e){
            result.setErrorMessage("更新评论状态失败！");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result deleteComment(Integer id){
        Result result=new Result();
        try{
            result=commentService.deleteComment(id);
        }catch (Exception e){
            result.setErrorMessage("删除评论失败！");
            e.printStackTrace();
        }
        return result;
    }
}
