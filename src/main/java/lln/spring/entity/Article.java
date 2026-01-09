package lln.spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 import java.util.Date;

 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 @TableName("t_article")
public class Article {
     @TableId(type = IdType.AUTO)
     private Integer id; // 文章ID
     private String title; // 文章标题
     private String content; // 文章内容
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date created; // 创建时间
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date modified; // 修改时间
     private String categories; // 文章分类
      private String tags; // 文章标签
     private Boolean allowComment; // 是否允许评论
     private String thumbnail; // 文章缩略图
     @TableField("author_id")
     private Integer authorId; // 作者ID
     
     // 用于表示当前用户是否点赞和收藏
     @TableField(exist = false)
     private Boolean liked = false; // 是否已点赞
     @TableField(exist = false)
     private Boolean favorited = false; // 是否已收藏

}
