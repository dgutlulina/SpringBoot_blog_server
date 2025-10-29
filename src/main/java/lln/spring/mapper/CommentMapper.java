package lln.spring.mapper;

import lln.spring.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT * FROM t_comment WHERE article_id=#{articleId} ORDER BY id DESC")
    public List<Comment> selectByArticleId(Integer articleId);
}
