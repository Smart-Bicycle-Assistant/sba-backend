package com.sba.recordingserver.config;

import com.sba.recordingserver.security.JwtAuthenticationFilter;
import com.sba.recordingserver.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // http 시큐리티 빌더
        http.cors()			// WebMvcConfig에서 이미 설정했으므로 기본 cors 설정
                .and()
                .csrf()					// csrf는 현재 사용하지 않으므로 disable
                .disable()
                .httpBasic()			// token을 사용하므로 basic 인증 disable
                .disable()
                .sessionManagement()	// session 기반이 아님을 선언
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()	// /와 /autho/** 경로는 인증 안해도 됨
                .antMatchers("/", "/member/**","/manager/**").permitAll()
                .anyRequest()			// /와 /auth/** 이외의 모든 경로는 인증 해야 됨
                .authenticated();

        // filter 등록
        // 매 요청마다
        // CorsFilter 실행한 후에
        // jwtAuthenticationFilter 실행한다.

        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
//        http.addFilterBefore(
//                jwtAuthenticationFilter,
//                CorsFilter.class
//        );
    }
    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new PasswordEncoder();
    }
}
