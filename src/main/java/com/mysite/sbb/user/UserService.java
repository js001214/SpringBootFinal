package com.mysite.sbb.user;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	//BCryptPasswordEncoder객체를 직접 생성하지 않고
	//빈으로 등록한 PasswordEncoder객체를 주입 받아 사용하도록 수정했다.
	
	
	
	public SiteUser Create(String username, String email, String password) {
		//User 데이터를 생성하는 Create메소드
		SiteUser user = new SiteUser();
	
		user.setUsername(username);
		
		user.setEmail(email);
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		//BCryptPasswordEncoder란 Spring Security에서 
		//비밀번호를 암호화하는데 사용할 수 있는 메소드를 가진 클래스이다.
		//passwordEncoder는 BCryptPasswordEncoder의 인터페이스이다.
		user.setPassword(passwordEncoder.encode(password));
		
		//user에 비밀번호를 암호화해서 등록
		
		this.userRepository.save(user);
		
		//user 정보(name , email, pass)를 JPA를 이용해 user에 담아 SiteUser에 리턴시킨다.
		
		return user;
		
	}
	
	//SiteUser를 조회할 수 있는 getUser메소드 
	public SiteUser getUser(String username) {
		
		Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
		//사용자명을 선택한 레코드를 siteUser에 담는다.
		if (siteUser.isPresent()) {
			//isPresent()는 Optinal 객체가 값을 가지고 있으면 실행, 값이 없으면 넘어간다.
			return siteUser.get(); //값을 SiteUser로 리턴
		} else {
			
			throw new DataNotFoundException("siteuser not found");
		}
	}
	
	
}
