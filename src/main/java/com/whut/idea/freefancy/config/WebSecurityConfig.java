package com.whut.idea.freefancy.config;

import com.whut.idea.freefancy.common.security.CustomizeAuthenticationEntryPoint;
import com.whut.idea.freefancy.common.security.CustomizeAuthenticationFailureHandler;
import com.whut.idea.freefancy.common.security.CustomizeAuthenticationSuccessHandler;
import com.whut.idea.freefancy.common.security.CustomizeLogoutSuccessHandler;
import com.whut.idea.freefancy.service.security.UserManageSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author LiMing
 * @date 2021/11/11 23:12
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public final static String SWAGGER_SOURCE_PERMIT_ALL = "/swagger-ui.html,/webjars/**,/swagger-resources/**,/v2/**";

    @Autowired
    CustomizeAuthenticationEntryPoint customizeAuthenticationEntryPoint;

    @Autowired
    CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler;

    @Autowired
    CustomizeAuthenticationFailureHandler customizeAuthenticationFailureHandler;

    @Autowired
    CustomizeLogoutSuccessHandler customizeLogoutSuccessHandler;

    @Bean
    public UserManageSecurityService userManageSecurityService(){
        return new UserManageSecurityService();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userManageSecurityService());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ?????????????????????
        http.exceptionHandling()
                .authenticationEntryPoint(customizeAuthenticationEntryPoint);
        // ??????
        http.formLogin().loginProcessingUrl("/public/login").permitAll()
                //????????????????????????
                .successHandler(customizeAuthenticationSuccessHandler)
                //????????????????????????
                .failureHandler(customizeAuthenticationFailureHandler);
        // ??????
        http.logout().logoutUrl("/public/logout").permitAll()
                //????????????????????????
                .logoutSuccessHandler(customizeLogoutSuccessHandler);
        // ??????
        http.authorizeRequests()
                // ??????????????????
                .antMatchers("/public/**").permitAll()
                // swagger??????????????????
                .antMatchers(SWAGGER_SOURCE_PERMIT_ALL.split(",")).permitAll()
                .anyRequest().authenticated();

        http.cors().and().csrf().disable();
    }
}
