package lln.spring.service.impl;

import lln.spring.entity.User;
import lln.spring.mapper.UserMapper;
import lln.spring.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2025-10-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User getUserInfoByUsername(String username) {
        return baseMapper.findByName(username);
    }
    
    @Override
    @Transactional
    public boolean updateUsername(Integer userId, String newUsername) {
        User user = new User();
        user.setId(userId);
        user.setUsername(newUsername);
        return updateById(user);
    }
    
    @Override
    @Transactional
    public boolean updateBio(Integer userId, String bio) {
        User user = new User();
        user.setId(userId);
        user.setBio(bio);
        return updateById(user);
    }
    
    @Override
    @Transactional
    public boolean updateAvatar(Integer userId, String avatarUrl) {
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        return updateById(user);
    }
    
    @Override
    @Transactional
    public boolean updatePassword(Integer userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        
        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }
    
    @Override
    @Transactional
    public boolean deleteUser(Integer userId) {
        return removeById(userId);
    }
    
    @Override
    public User getUserProfile(Integer userId) {
        return getById(userId);
    }
}
