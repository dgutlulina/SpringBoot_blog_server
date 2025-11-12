package lln.spring.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSearch {
    private ArticleCondition articleCondition;
    private PageParams pageParams;
}
