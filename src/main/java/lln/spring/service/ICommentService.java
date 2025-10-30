package lln.spring.service;

import lln.spring.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
public interface ICommentService extends IService<Comment> {
    
    /**
     * 添加评论
     * @param comment 评论对象
     * @return 添加后的评论对象
     */
    Comment addComment(Comment comment);
}
