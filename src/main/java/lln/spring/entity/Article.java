package lln.spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     private Date created; // 创建时间
     private Date modified; // 修改时间
     private String categories; // 文章分类
      private String tags; // 文章标签
     private Boolean allowComment; // 是否允许评论
     private String thumbnail; // 文章缩略图

}
