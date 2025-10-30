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
        return result;
    }
}
