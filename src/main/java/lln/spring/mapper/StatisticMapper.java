package lln.spring.mapper;

import lln.spring.entity.Statistic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Mapper
public interface StatisticMapper extends BaseMapper<Statistic> {

    @Select("select * from t_statistic where article_id=#{articleId}")
    public Statistic selectByArticleId(Integer articleId);

    @Select("select sum(hits) from t_statistic")
    public Integer selectTotalHits();

}
