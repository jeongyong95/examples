package com.joojeongyong.jwt.configuration;

import com.joojeongyong.jwt.filter.MySecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final MySecurityFilter securityFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                Security FilterChain이 밖에서 등록한 필터보다 먼저 실행됨
                .addFilterBefore(securityFilter, BasicAuthenticationFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter)
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeRequests()
                    .antMatchers("/api/v1/user/**").hasRole("{USER,MANAGER,ADMIN}")
                    .antMatchers("/api/v1/manager/**").hasRole("{MANAGER,ADMIN}")
                    .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll();
    }
}
