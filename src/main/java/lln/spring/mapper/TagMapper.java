package lln.spring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lln.spring.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}