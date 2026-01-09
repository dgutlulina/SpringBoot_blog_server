package lln.spring.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleVO {
    private Integer id;
    private String title;
    private Date created;
    private String categories;
    private String thumbnail; // 文章缩略图

    private Integer hits;
    private Integer heartCount;
    private boolean isLiked;
    private boolean isFavorited;
}
