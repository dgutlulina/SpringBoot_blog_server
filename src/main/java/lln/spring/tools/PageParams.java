package lln.spring.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {
//    页号
    private Long page = 1L;

//    每行页数
    private Long rows = 2L;

    //总记录数
    private Long total;

}
