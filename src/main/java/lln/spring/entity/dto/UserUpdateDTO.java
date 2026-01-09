package lln.spring.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    private String username;
    
    @Size(max = 200, message = "个人简介长度不能超过200个字符")
    private String bio;
    
    private String avatar;
}