package org.yeachan.spring_security.config;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.yeachan.spring_security.account.AccountService;
import org.yeachan.spring_security.common.LoggingFilter;

import java.util.function.Supplier;

@Configuration
public class SecurityConfig {
    @Autowired
    private AccountService accountService;
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
        
        http.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class);

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
                        .loginPage("/login")
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionFixation().changeSessionId()
                        .invalidSessionUrl("/login")
                        .maximumSessions(1).maxSessionsPreventsLogin(true)//세션 점유시 추가 로그인 불가
                )
                .exceptionHandling(exceptionHandle -> exceptionHandle.accessDeniedHandler((request, response, accessDeniedException) -> {
                    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = principal.getUsername();
                    System.out.println(username + "is denied to access " + request.getRequestURI());
                    response.sendRedirect("/access-denied");
                }))
                .rememberMe(rememberMe->rememberMe

                        .userDetailsService(accountService)
                        .key("remember-me-sample"))
        ;
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
