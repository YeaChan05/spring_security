package org.yeachan.spring_security.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.util.function.Supplier;

@Configuration
public class SecurityConfig {
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public SecurityExpressionHandler<FilterInvocation> webSecurityExpressionHandlerImpl() {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        return handler;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        RequestCache nullRequestCache = new NullRequestCache();

        http.authorizeHttpRequests(registry ->//http 요청에 대한 인가 설정
                                registry.requestMatchers("/", "/info", "/account/**", "/signup").permitAll()
                                        .requestMatchers("/admin").access(this::authorizationDecision)
                                        .requestMatchers("/user").hasRole("USER")
//                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()//이렇게하면 filter를 전부 다 탄다.
                                        .anyRequest().authenticated()
                )
                .requestCache((cache) -> cache
                        .requestCache(nullRequestCache)
                )
                .formLogin(login -> login
                        .defaultSuccessUrl("/"))
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/")
                );
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return http.build();
    }

    private AuthorizationDecision authorizationDecision(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext requestAuthorizationContext) {
        if (authenticationSupplier.get().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return new AuthorizationDecision(true);
        }
        return new AuthorizationDecision(false);
    }

}
