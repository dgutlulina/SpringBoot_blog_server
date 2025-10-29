package lln.spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
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
@TableName("t_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的文章id
     */
    private Integer articleId;

    /**
     * 评论时间
     */
    private LocalDate created;

    /**
     * 评论用户登录的ip地址
     */
    private String ip;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论状态
     */
    private String status;

    /**
     * 评论用户用户名
     */
    private String author;
}
