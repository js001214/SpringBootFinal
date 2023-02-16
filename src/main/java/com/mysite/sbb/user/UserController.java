package com.mysite.sbb.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService; 
	
	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		
		return "signup_form";
	}
	//userCreateForm을 이용해 값을 입력후 singup_form에 넘겨준다.
	
	
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult)
	{
		if(bindingResult.hasErrors()) {
			
			return "signup_form";
		}
		//userCreateForm 값에 에러가 있을지 sinup_form에 리턴해준다.
		
	
	
	if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
		
		bindingResult.rejectValue("password2", "passwordInCorrect",
				"2개의 패스워드가 일치하지 않습니다.");
		
		//bindingResult.rejectValue(필드명, 오류코드, 에러메시지)
		
		return "signup_form";
		
	}
	
	try {
		userService.Create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
	}catch(DataIntegrityViolationException e) {
		
		e.printStackTrace();
		//e.printStackTrace : 에러의 원인을 단계별로 자세히 출력한다.
		
		bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
		
		return "signup_form";
	
	}catch(Exception e) {
		
		e.printStackTrace();
		
		bindingResult.reject("signupFailed", e.getMessage());
		
		//e.getMessage() :에러의 원인을 간단하게 출력한다.
		
		 return "signup_form";
	}
	
	

	
	
	
	//userCreateForm의 pass1과 pass2의 값이 다르면 문자를 출력하면서 signup_form으로 리턴한다.
	
	userService.Create(userCreateForm.getUsername(), 
			
			userCreateForm.getEmail(), userCreateForm.getPassword1());
	
	return "redirect:/";
	}
	
	//userService.Create()메소드는 User데이터를 생성하는 메소드이며, setter로 값을 받는다.
	//getter로 값을 출력후 / 주소로 이동한다.
	//redirect:/주소는 해당 주소로 URL요청을 다시 하는 것이다.
	//단 화면 넘기기만 가능하고 데이터를 전달하지는 못한다.
	
	//return "view"는 단순히 지정한 view 페이지를 보여주는 것이다.
	
	@GetMapping("/login")
	 public String login() {
	 return "login_form";
	 }
	
}
