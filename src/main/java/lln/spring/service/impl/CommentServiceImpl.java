package lln.spring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lln.spring.entity.Comment;
import lln.spring.mapper.CommentMapper;
import lln.spring.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

@Autowired
private CommentMapper commentMapper;

public Comment insert(Comment comment){
    commentMapper.insert(comment);
    return comment;
}

public Result getAPageCommentByArticleId(Integer articleId, PageParams pageParams){
    //查询条件构造器QueryWrapper
    QueryWrapper wrapper = new QueryWrapper<>();
    wrapper.eq("article_id",articleId);
    wrapper.orderBy(true, false, "id");
    // 创建分页对象
    Page<Comment> page = new Page<>(pageParams.getPage(), pageParams.getRows());
    Page<Comment> aPage = commentMapper.selectPage(page, wrapper);
    Result result=new Result();
    result.getMap().put("comments",aPage.getRecords());
    result.getMap().put("total",aPage.getTotal());
    return result;
}

@Override
public Result getAllComments(PageParams pageParams) {
    // 创建分页对象
    Page<Comment> page = new Page<>(pageParams.getPage(), pageParams.getRows());
    // 查询所有评论，按时间倒序排列
    QueryWrapper<Comment> wrapper = new QueryWrapper<>();
    wrapper.orderByDesc("created");
    Page<Comment> commentPage = commentMapper.selectPage(page, wrapper);
    
    Result result = new Result();
    result.getMap().put("comments", commentPage.getRecords());
    result.getMap().put("total", commentPage.getTotal());
    result.setMsg("获取评论列表成功！");
    return result;
}

@Override
public Result updateCommentStatus(Integer id, String status) {
    Result result = new Result();
    try {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setStatus(status);
            commentMapper.updateById(comment);
            result.setMsg("更新评论状态成功！");
        } else {
            result.setErrorMessage("评论不存在！");
        }
    } catch (Exception e) {
        result.setErrorMessage("更新评论状态失败！");
        e.printStackTrace();
    }
    return result;
}

@Override
public Result deleteComment(Integer id) {
    Result result = new Result();
    try {
        int count = commentMapper.deleteById(id);
        if (count > 0) {
            result.setMsg("删除评论成功！");
        } else {
            result.setErrorMessage("评论不存在！");
        }
    } catch (Exception e) {
        result.setErrorMessage("删除评论失败！");
        e.printStackTrace();
    }
    return result;
}
}
