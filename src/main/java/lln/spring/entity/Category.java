package lln.spring.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_category")
public class Category {
    @TableId
    private Integer id;
    private String name;
    private java.util.Date created;
}