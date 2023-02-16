package com.mysite.sbb.user;

import lombok.Getter;

@Getter
public enum UserRole {
	//사용자를 조회하는 서비스(UserSecurityService)를 만들기전에 필요한 메소드를 만들어준다.
	//enum은 열거 자료형이다.
	ADMIN("ROLE_ADMIN"),
	//ADMIN은 ROLE_ADMIN의 값을 가지고 있다.
	USER("ROLE_USER");
	//ADMIN은 ROLE_USER의 값을 가지고 있다.
	//상수 자료형이므로 @Getter만 사용가능하도록 했다. ? 왜 상수자료형이라는 이유만으로 그래?
	
	UserRole(String value) {
	
	this.value = value;
	}
	
	private String value;

}
