package com.example.securitydemo.config;

import com.example.securitydemo.model.Authority;
import com.example.securitydemo.repository.AuthorityRepository;
import com.example.securitydemo.security.jwt.Constants;
import com.example.securitydemo.security.jwt.TokenProvider;
import com.example.securitydemo.security.jwt.JWTConfigurer;
import com.example.securitydemo.service.AuthorityService;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableWebSecurity
@Slf4j
/**
 * securedEnabled，开启@Secured断言 ， @Secured("ROLE_ADMIN")
 * jsr250Enabled, 开启 @RolesAllowed 断言，@RolesAllowed("ROLE_ADMIN")
 * prePostEnabled, 开启通过@PreAuthorize 和 @PostAuthorize 控制的更复杂的权限控制表达式
 *  @PreAuthorize("isAnonymous()") ， @PreAuthorize("hasRole('USER')")
 */
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * AuthenticationManager 是 Spring Security 中实现用户认证的主要接口 AuthenticationManagerBuilder 负责生成 AuthenticationManager 可以通过AuthenticationManagerBuilder 建立基于内存的认证,
     * LDAP 认证, JDBC认证, 或者添加自己的自定义认证. 此例子中, 我们提供 customUserDetailsService 和 passwordEncoder来构建AuthenticationManager. 通过配置好的AuthenticationManager在登录api中认证用户.
     */
    @Resource
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private CorsFilter corsFilter;

    @Resource
    private TokenProvider tokenProvider;

    @Resource
    private AppFilterInvocationSecurityMetadataSource mySecurityMetadataSource;

    @Resource
    private AppAccessDecisionManager myAccessDecisionManager;

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder.userDetailsService(userDetailsService)
                                        .passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 不需要权限校验的
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
           .antMatchers(HttpMethod.OPTIONS, "/**")
           .antMatchers("/app/**/*.{js,html}")
           .antMatchers("/i18n/**")
           .antMatchers("/content/**")
           .antMatchers("/h2-console/**")
           .antMatchers("/swagger-ui/index.html")
           .antMatchers("/test/**");
    }

    /**
     * 此处可添加 .exceptionHandling() 可自定义 认证入口点 .authenticationEntryPoint(problemSupport) 可自定义 权限异常处理 .accessDeniedHandler(problemSupport)
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 由于使用的是JWT，我们这里不需要csrf
            // POST请求 会默认会开启CSRF处理，判断请求是否携带了token，如果没有就拒绝访问，所以需关闭
            .csrf().disable()
            // OncePerRequestFilter，确保在一次请求中只通过一次filter，配置跨域与权限校验filter
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            // 禁止 x-frame-options（运行同一个域名中的任何请求）属性
            .headers().frameOptions().disable()
            // 基于token，所以不需要session
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authorizeRequests()
            // 登录和注册 api允许匿名访问
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .antMatchers(HttpMethod.POST, "/registry").permitAll()
            // /api/users 都必须要USER权限
//            .antMatchers("/api/users/**").hasAnyAuthority(Constants.USER)
            .antMatchers("/api/**").authenticated()
            .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                @Override
                public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                    o.setSecurityMetadataSource(mySecurityMetadataSource);
                    o.setAccessDecisionManager(myAccessDecisionManager);
                    return o;
                }
            })
            // 这里增加securityConfigurerAdapter
            .and().apply(securityConfigurerAdapter());


//        if (map != null && map.size() > 0) {
//            for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
//                http.authorizeRequests().antMatchers(entry.getKey()).hasAnyAuthority(Joiner.on(",").join(entry.getValue())).and();
//            }
//        }
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

}
