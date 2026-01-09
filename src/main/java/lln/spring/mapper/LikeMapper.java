package lln.spring.mapper;

import lln.spring.entity.Like;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户点赞表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {
    
    @Select("SELECT * FROM t_like WHERE user_id = #{userId}")
    List<Like> selectByUserId(@Param("userId") Integer userId);
    
    @Select("SELECT * FROM t_like WHERE user_id = #{userId} AND article_id = #{articleId}")
    Like selectByUserIdAndPostId(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    
    @Select("SELECT COUNT(*) FROM t_like WHERE article_id = #{articleId}")
    Integer countByPostId(@Param("articleId") Integer articleId);
    
    /**
     * 根据文章ID和用户ID查询点赞记录
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    @Select("SELECT * FROM t_like WHERE article_id = #{articleId} AND user_id = #{userId}")
    Like findByPostIdAndUserId(@Param("articleId") Integer articleId, @Param("userId") Integer userId);
    
    /**
     * 根据用户ID查询点赞的文章ID列表
     * @param userId 用户ID
     * @return 点赞的文章ID列表
     */
    @Select("SELECT article_id FROM t_like WHERE user_id = #{userId}")
    List<Integer> findLikedPostIdsByUserId(@Param("userId") Integer userId);
}