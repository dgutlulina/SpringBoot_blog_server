package lln.spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Getter
@Setter
@TableName("t_statistic")
public class Statistic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的文章id
     */
    private Integer articleId;

    /**
     * 文章点击总量
     */
    private Integer hits;

    /**
     * 文章评论总量
     */
    private Integer commentsNum;

    /**
     * 点赞数
     */
    private Integer heartCount;
}
