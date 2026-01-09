package lln.spring.controller;

import lln.spring.entity.User;
import lln.spring.entity.dto.PasswordUpdateDTO;
import lln.spring.entity.dto.UserUpdateDTO;
import lln.spring.entity.vo.UserInfoVO;
import lln.spring.service.IUserService;
import lln.spring.service.ArticleService;
import lln.spring.tools.PageParams;
import lln.spring.tools.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private IUserService userService;
    
    @Autowired
    private ArticleService articleService;

    @Value("${uploadAvatarsDir}")
    private String uploadAvatarsDir;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            User user = userService.getUserInfoByUsername(username);
            if (user != null) {
                UserInfoVO userInfoVO = new UserInfoVO();
                BeanUtils.copyProperties(user, userInfoVO);
                return Result.success(userInfoVO);
            }
        }
        return Result.error("用户未登录或不存在");
    }

    /**
     * 获取指定用户资料
     */
    @GetMapping("/{userId}")
    public Result getUserProfile(@PathVariable Integer userId) {
        User user = userService.getUserProfile(userId);
        if (user != null) {
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(user, userInfoVO);
            return Result.success(userInfoVO);
        }
        return Result.error("用户不存在");
    }

    /**
     * 更新用户名
     */
    @PostMapping("/username")
    public Result updateUsername(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        boolean success = userService.updateUsername(currentUser.getId(), userUpdateDTO.getUsername());
        if (success) {
            return Result.success("用户名更新成功");
        }
        return Result.error("用户名更新失败");
    }

    /**
     * 更新个人简介
     */
    @PostMapping("/bio")
    public Result updateBio(@RequestBody UserUpdateDTO userUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        boolean success = userService.updateBio(currentUser.getId(), userUpdateDTO.getBio());
        if (success) {
            return Result.success("个人简介更新成功");
        }
        return Result.error("个人简介更新失败");
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        // 文件验证
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只支持图片格式文件");
        }
        
        // 检查文件大小 (限制为5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            return Result.error("文件大小不能超过5MB");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 确定存储路径
            File uploadPath = new File(uploadAvatarsDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            
            // 保存文件
            String filePath = uploadAvatarsDir + newFilename;
            file.transferTo(new File(filePath));
            
            // 更新用户头像URL
            String avatarUrl = "/api/images/avatars/" + newFilename;
            boolean success = userService.updateAvatar(currentUser.getId(), avatarUrl);
            if (success) {
                return Result.success(avatarUrl, "头像上传成功");
            } else {
                return Result.error("头像更新失败");
            }
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 更新密码
     */
    @PostMapping("/password")
    public Result updatePassword(@Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        // 验证两次输入的新密码是否一致
        if (!passwordUpdateDTO.getNewPassword().equals(passwordUpdateDTO.getConfirmNewPassword())) {
            return Result.error("两次输入的新密码不一致");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        boolean success = userService.updatePassword(currentUser.getId(), passwordUpdateDTO.getOldPassword(), passwordUpdateDTO.getNewPassword());
        if (success) {
            return Result.success("密码更新成功");
        } else {
            return Result.error("原密码错误或密码更新失败");
        }
    }

    /**
     * 删除用户账户
     */
    @DeleteMapping("")
    public Result deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        boolean success = userService.deleteUser(currentUser.getId());
        if (success) {
            return Result.success("账户删除成功");
        }
        return Result.error("账户删除失败");
    }
    
    /**
     * 获取我的帖子
     */
    @PostMapping("/articles")
    public Result getMyArticles(@RequestBody PageParams pageParams) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        return articleService.getUserArticles(currentUser.getId(), pageParams);
    }
    
    /**
     * 获取我喜欢的帖子
     */
    @PostMapping("/liked-articles")
    public Result getLikedArticles(@RequestBody PageParams pageParams) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        return articleService.getUserLikedArticles(currentUser.getId(), pageParams);
    }
    
    /**
     * 获取我的收藏
     */
    @PostMapping("/favorited-articles")
    public Result getFavoritedArticles(@RequestBody PageParams pageParams) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Result.error("用户未登录");
        }
        
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserInfoByUsername(currentUsername);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        return articleService.getUserFavoritedArticles(currentUser.getId(), pageParams);
    }
}