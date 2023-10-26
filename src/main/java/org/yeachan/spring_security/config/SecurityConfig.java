package org.yeachan.spring_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry ->//http 요청에 대한 인가 설정
                        registry.requestMatchers("/", "/info","/account/**").permitAll()
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

//    @Bean
//    @Order(Ordered.LOWEST_PRECEDENCE - 15)
//    public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/account/**")
//                .authorizeHttpRequests(registry -> registry
//                        .anyRequest().permitAll())
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults());
//
//        return http.build();
//    }
}
