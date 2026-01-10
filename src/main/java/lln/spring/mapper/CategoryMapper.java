package lln.spring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lln.spring.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}