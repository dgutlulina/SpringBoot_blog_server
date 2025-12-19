package lln.spring.entity.dto;

import lln.spring.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private Boolean valid;
    List<String> authorities = new ArrayList<>(); //权限

    //将DTO对象转为实体类对象
    public static void dtoToEntity(UserDTO dto, User entity) {
        //复制同名的属性值
        BeanUtils.copyProperties(dto, entity);
    }

    //将实体类对象转换为DTO对象
    public static UserDTO entityToDto(User entity) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(entity,dto);
        return dto;
    }
}