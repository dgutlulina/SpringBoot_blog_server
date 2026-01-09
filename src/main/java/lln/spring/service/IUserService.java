package lln.spring.service;

import lln.spring.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
public interface IUserService extends IService<User> {
    
    /**
     * 根据用户名获取用户信息（不包含密码）
     */
    User getUserInfoByUsername(String username);
    
    /**
     * 更新用户昵称
     */
    boolean updateUsername(Integer userId, String newUsername);
    
    /**
     * 更新用户简介
     */
    boolean updateBio(Integer userId, String bio);
    
    /**
     * 更新用户头像
     */
    boolean updateAvatar(Integer userId, String avatarUrl);
    
    /**
     * 更新用户密码
     */
    boolean updatePassword(Integer userId, String oldPassword, String newPassword);
    
    /**
     * 删除用户账户
     */
    boolean deleteUser(Integer userId);
    
    /**
     * 获取用户详细资料
     */
    User getUserProfile(Integer userId);
}
