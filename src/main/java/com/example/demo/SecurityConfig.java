package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfiguration {

    @SuppressWarnings("deprecation")
	protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .requestMatchers("/**").permitAll() // すべてのリクエストに対してアクセスを許可
                .and()
            .formLogin()
                .loginPage("/show")
                .permitAll()
                .and()
            .logout()
                .permitAll();
        
        // CSRFを無効化（あるいは必要な場合に有効化）
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // ここで利用するPasswordEncoderをカスタマイズ
        return new BCryptPasswordEncoder();
    }
}
