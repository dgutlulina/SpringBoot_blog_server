package lln.spring.config.security;


import lln.spring.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override//用于查找用户及其密码、权限等信息
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //此处有2个User类，注意区分，此处的User类为代表数据库t_user表的实体类
        lln.spring.entity.User user = userMapper.findByNameWithValid(username);

        if (null == user)
            throw new UsernameNotFoundException(username);
        //查找用户拥有的权限（角色）
        List<String> authorityNames=userMapper.findAuthorityByName(username);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for(String authorityName:authorityNames)
            authorities.add(new SimpleGrantedAuthority(authorityName));
        //此处的User类为Security的类
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}