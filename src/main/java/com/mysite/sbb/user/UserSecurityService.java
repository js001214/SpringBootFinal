package com.mysite.sbb.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
											//form에서 넘겨받은 username
		//UserDetails는 구현해야할 User객체이다.
		
		Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);
			//_siteUser는 입력한username레코드의 값을 가지고 있다.
		if(_siteUser.isEmpty()) {
			//_siteUser의 값이 없을때
			throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
		}
		
		//폼에서 넘어오는 username을 DB에서 쿼리해서 siteUser객체에 담은 값을 Optional에서 뽑아온다.
		SiteUser siteUser = _siteUser.get();
		//_siteUser의 Optional을 가져와서 siteUser객체에 담는다.
		
		//Authentication (인증) : Identity (ID) + Password를 확인 하는것
		//Authorization	(허가)  : 인증된 사용자에게 사이트의 서비스를 쓸수 있도록 권한을 부여하는 것
			// 계정이 admin 이라면 ADMIN Role을 적용
			// 계정이 admin이 아니라면 User Role을 적용
		List<GrantedAuthority> authorities = new ArrayList<>();	//ArrayList는 크기가 가변적이다.
		//GrantedAuthority는 현재 사용자가 가지고 있는 권한이다.
		
		if ("admin".equals(username)) {
			
			authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
			//admin이라면 admin정보를
		}else {
			
			authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
			//admin이 아니라면 user정보를 넣는다.
		}
		
		return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);	
				
	}	
}
