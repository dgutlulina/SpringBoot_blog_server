package lln.spring.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lln.spring.entity.Article;
import lln.spring.entity.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

import java.util.List;


@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Select("SELECT t_article.id,title,t_article.created,categories,t_statistic.hits FROM t_article , t_statistic ${ew.customSqlSegment}")
    IPage<ArticleVO> getAPageOfArticleVO(IPage<ArticleVO> page, @Param("ew") Wrapper wrapper);

    public List<Article> getPage(@Param("offset")Integer offset, @Param("size")Integer size);


}
