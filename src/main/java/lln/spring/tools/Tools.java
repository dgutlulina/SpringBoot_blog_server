package lln.spring.tools;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Tools {
    //将Date转换为LocalDate类型，固定用法
    public static LocalDate dateToLocalDate(Date date){
        Instant instant=date.toInstant();
        ZoneId zoneId= ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }
}
