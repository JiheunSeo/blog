package com.test.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.test.blog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.test.blog.config.oauth.OAuth2SuccessHandler;
import com.test.blog.config.jwt.TokenProvider;
import com.test.blog.config.oauth.OAuth2UserCustomService;
import com.test.blog.repository.RefreshTokenRepository;
import com.test.blog.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
	private final OAuth2UserCustomService oAuth2UserCustomService;
	private final TokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserService userService;
	
	@Bean
	public WebSecurityCustomizer configure() {
		return (web) -> web.ignoring()
				.requestMatchers("/img/**")
                .requestMatchers("/css/**")
                .requestMatchers("/js/**");
	}
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
                    throws Exception {
            http.csrf(csrf -> csrf.disable())
                            .httpBasic(httpBasic -> httpBasic.disable())
                            .formLogin(formLogin -> formLogin.disable())
                            .logout(logout -> logout.disable());

            http.sessionManagement(
                            sessionManagement -> sessionManagement
                                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            // 헤더를 확인할 커스텀 필터 추가
            http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

            // 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요.
            http
                            .authorizeHttpRequests(authorize -> authorize
                                            .requestMatchers(new MvcRequestMatcher(introspector, "/api/token"))
                                            .permitAll()
                                            .requestMatchers(new MvcRequestMatcher(introspector, "/api/**"))
                                            .authenticated()
                                            .anyRequest().permitAll());

            http.oauth2Login(oauth2Login -> oauth2Login
                            .loginPage("/login")
                            // Authorization 요청과 관련된 상태 저장
                            .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                                            .authorizationRequestRepository(
                                                            oAuth2AuthorizationRequestBasedOnCookieRepository()))
                            .successHandler(oAuth2SuccessHandler()) // 인증 성공 시 실행할 핸들러
                            .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                            .userService(oAuth2UserCustomService)));

            http
                            .logout((logout) -> logout.logoutSuccessUrl("/login"));

            // /api로 시작하는 url인 경우 401 상태 코드를 반환하도록 예외 처리
            http.exceptionHandling(exceptionHandling -> exceptionHandling
                            .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                            new AntPathRequestMatcher("/api/**")));

            return http.build();
    }
	
	@Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                        refreshTokenRepository,
                        oAuth2AuthorizationRequestBasedOnCookieRepository(),
                        userService);
}

@Bean
public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
}

@Bean
public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
}

@Bean
public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
}
	
}
