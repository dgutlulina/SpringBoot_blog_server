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

    /**
     * 获取所有评论的分页列表
     * @param pageParams 分页参数
     * @return 评论列表
     */
    public Result getAllComments(PageParams pageParams);

    /**
     * 更新评论状态
     * @param id 评论ID
     * @param status 新状态
     * @return 更新结果
     */
    public Result updateCommentStatus(Integer id, String status);

    /**
     * 删除评论
     * @param id 评论ID
     * @return 删除结果
     */
    public Result deleteComment(Integer id);

}
