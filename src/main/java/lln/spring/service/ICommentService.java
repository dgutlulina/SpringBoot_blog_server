package lln.spring.service;

import lln.spring.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
public interface ICommentService extends IService<Comment> {

    public Result getAPageCommentByArticleId(Integer articleId, PageParams pageParams);

    public Comment insert(Comment comment);

}
