package lln.spring.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lln.spring.tools.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //启用方法级的权限认证
public class SecurityConfig { //权限配置

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/api/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/article/articleSearch")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/article/getIndexData")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/article/getAPageOfArticle")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/article/getArticleAndFirstPageCommentByArticleId")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/article/getArticleById")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/article/selectById")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/article/getAPageOfArticleVO")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/comment/insert")).permitAll() //任意访问
                // 仅管理员可访问的接口
                .requestMatchers(new AntPathRequestMatcher("/article/deleteById")).hasRole("admin")
                .requestMatchers(new AntPathRequestMatcher("/article/getUserArticles")).hasRole("admin")
                .requestMatchers(new AntPathRequestMatcher("/article/publish")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/article/publishArticle")).authenticated() // 认证用户可访问
                // 其他请求需要认证
                .anyRequest().authenticated()
        )
        .formLogin(form -> form
                // 1) 自定义登录认证
                .loginProcessingUrl("/login") // 指定登录处理URL
                .failureHandler(myAuthenticationFailureHandler) //登录失败处理器
                .successHandler(myAuthenticationSuccessHandler) //登录成功处理器
                .permitAll()
        )
        .logout(logout -> logout
                // 配置退出登录
                .logoutUrl("/logout") //退出路径
                .logoutSuccessHandler(new LogoutSuccessHandler() { //注销用户成功时执行
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                                org.springframework.security.core.Authentication authentication) throws IOException, ServletException {
                        request.getSession().removeAttribute("user");
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write(objectMapper.writeValueAsString(
                                new Result(true, "登出成功")
                        ));
                    }
                })
        )
        .cors(cors -> cors.and()) // 启用CORS支持
        .csrf(csrf -> csrf.disable())//禁用跨站csrf攻击防御
        .headers(headers -> headers.frameOptions().disable()); //防止错误: Refused to display in a frame because it set 'X-Frame-Options' to 'DENY'

        return http.build();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(this.myUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt密码编码器
        return new BCryptPasswordEncoder();
    }
}