package org.yeachan.spring_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/")// 해당 url로 요청 할 때 적용되는 filter 설정
                .authorizeHttpRequests(registry -> registry//http 요청에 대한 인가 설정
                        .requestMatchers("/", "/info", "/account/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 15)
    public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/account/**")
                .authorizeHttpRequests(registry -> registry
                        .anyRequest().permitAll())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
