package lln.spring.mapper;

import lln.spring.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM t_user WHERE username=#{username}")
    public User findByName(String username);

    //根据用户名查询未被禁用的用户信息
    @Select("SELECT * FROM t_user WHERE username=#{username} and valid=1")
    public User findByNameWithValid(String username);

    //根据用户名查询用户拥有的权限（t_authority表中的authority）
    // 涉及到3张表：t_user,t_authority和t_user_authority
    @Select("select a.authority from t_user u,t_authority a," +
            "t_user_authority ua where ua.user_id=u.id " +
            "and ua.authority_id=a.id and u.username =#{username}")
    public List<String> findAuthorityByName(String username);

}
