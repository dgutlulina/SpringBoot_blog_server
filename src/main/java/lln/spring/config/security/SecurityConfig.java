package lln.spring.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lln.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //启用方法级的权限认证
public class SecurityConfig extends WebSecurityConfigurerAdapter { //权限配置

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 允许匿名访问的接口
                .antMatchers("/images/**", "/article/articleSearch", "/article/getIndexData",
                        "/article/getPageOfArticle", "/article/getIndexData",
                        "/article/getPageOfArticleByArticleId", "/article/getArticleById",
                        "/article/selectById", "/article/getPageOfCommentByArticleId", 
                        "/comment/insert").permitAll() //任意访问
                // 仅管理员可访问的接口
                .antMatchers("/article/delete/getById", "/article/getPageOfArticleByU",
                        "/article/publish", "/article/publishArticle").hasRole("admin") //管理员权限
                // 仅普通会员可访问的接口
                .antMatchers("/blog/insert").hasRole("common") //普通会员权限
                // 其他请求需要认证
                .anyRequest().authenticated()
                .and()
                // 1) 自定义登录认证
                .formLogin()
                .loginProcessingUrl("/login") // 指定登录处理URL
                .failureHandler(myAuthenticationFailureHandler) //登录失败处理器
                .successHandler(myAuthenticationSuccessHandler) //登录成功处理器
                .permitAll()
                .and()
                // 配置退出登录
                .logout()
                .logoutUrl("/logout") //退出路径
                .logoutSuccessHandler(new LogoutSuccessHandler() { //注销用户成功时执行
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                        request.getSession().removeAttribute("user");
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write(objectMapper.writeValueAsString(
                                new Result(true, "登出成功")
                        ));
                    }
                })
                .and().cors() // 启用CORS支持
                .and().csrf().disable();//禁用跨站csrf攻击防御

//防止错误: Refused to display in a frame because it set 'X-Frame-Options' to 'DENY'
        http.headers().frameOptions().disable();

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 配置用户详情服务和密码编码器
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt密码编码器
        return new BCryptPasswordEncoder();
    }
}