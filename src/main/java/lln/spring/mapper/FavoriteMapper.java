package lln.spring.mapper;

import lln.spring.entity.Favorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户收藏表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    
    @Select("SELECT * FROM t_favorite WHERE user_id = #{userId}")
    List<Favorite> selectByUserId(@Param("userId") Integer userId);
    
    @Select("SELECT * FROM t_favorite WHERE user_id = #{userId} AND article_id = #{articleId}")
    Favorite selectByUserIdAndPostId(@Param("userId") Integer userId, @Param("articleId") Integer articleId);
    
    @Select("SELECT COUNT(*) FROM t_favorite WHERE article_id = #{articleId}")
    Integer countByPostId(@Param("articleId") Integer articleId);
    
    /**
     * 根据文章ID和用户ID查询收藏记录
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    @Select("SELECT * FROM t_favorite WHERE article_id = #{articleId} AND user_id = #{userId}")
    Favorite findByPostIdAndUserId(@Param("articleId") Integer articleId, @Param("userId") Integer userId);
    
    /**
     * 根据用户ID查询收藏的文章ID列表
     * @param userId 用户ID
     * @return 收藏的文章ID列表
     */
    @Select("SELECT article_id FROM t_favorite WHERE user_id = #{userId}")
    List<Integer> findFavoritePostIdsByUserId(@Param("userId") Integer userId);
}