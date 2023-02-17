package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration		//스프링의 환경설정 파일임을 의미하는 어노테이션
@EnableWebSecurity	//모든 요청 URL이 스프링 시큐리티의 제어를 받도록하는 어노테이션
@EnableMethodSecurity(prePostEnabled = true)	// @어노테이션의 prePostEnabled = true 설정은 Question,AnswerController에서 
												//로그인 여부를 판별하기 위해 사용해던 @PreAuthorize 을 사용하기 위해 필요하다.
public class SecurityConfig {

	@Bean
	
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests().requestMatchers(
			new AntPathRequestMatcher("/**")).permitAll()	//인증되지 않은 요청을 허락 한다는 뜻
		
		.and()
		.csrf().ignoringRequestMatchers(
				 new AntPathRequestMatcher("/h2-console/**"))
				 
		
		.and()
		
		 .headers()
		
		.addHeaderWriter(new XFrameOptionsHeaderWriter(
				
				XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
		//----------로그인 처리		
		.and()
		
			.formLogin()
			
			.loginPage("/user/login") //로그인 페이지의 URL
			
			.defaultSuccessUrl("/") //로그인 성공시 default page 루트URL(/)로 이동한다.
		//SpringSecurity의 로그인 설정을 담당하는 부분
		
		//-----------
		
		//-----------로그아웃 처리
		.and()
		
			.logout()
			
			.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
		
			.logoutSuccessUrl("/")
			
			.invalidateHttpSession(true); //로그아웃시 생성된 사용자 세션을 삭제한다.
		//-----------
		
		return http.build();
	}
	
	@Bean
	
	PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
			
		
	}
	
	
	@Bean
	 AuthenticationManager authenticationManager(AuthenticationConfiguration 
	authenticationConfiguration) throws Exception {
		
	 return authenticationConfiguration.getAuthenticationManager();
	
	 //AuthenticationManager빈을 생성. Spring Security의 인증을 담당.
	 //UserSecurityService와 PasswordEncoder가 자동으로 설정된다.
	}
	
	
}
