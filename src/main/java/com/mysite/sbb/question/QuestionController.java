package com.mysite.sbb.question;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor	//final 필드의 생성자를 자동으로 만들어서 생성자를 만
@Controller	//QuestionController를 객체화해서 framework에 만들어준다.
public class QuestionController {
	/* 클래스를 객체로 생성 어노테이션 (빈(객체) 등록, Spring Framework)
	  @Component : 일반적인 클래스를 객체화
	  @Controller : 클라이언트 요청을 받아서 처리, Controller
	  		1. 클라이언트 요청을 받는다. @GetMapping, @PostMapping
	  		2. 비즈니스 로직 처리, Service의 메소드 호출,
	  		3. View 페이지로 응답
	  		
	  @Service : DAO의 메소드를 인터페이스로 생성후 인터페이스의 메소드를 구현한 클래스
	  	- 유지보수를 쉽게 하기 위해서 (약결합)
	  @Repository : DAO 클래스를 빈등록
	  
	  
	 */
	/* DI (의존성 주입)
	 1. @Autowired: Spring Framework에 생성된 빈(객체)을 주입, 타입을 찾아서 주입
	  		같은 타입의 객체가 존재할 경우 문제가 발생될 수 있다.
	 2. 생성자를 통한 의존성 주입(권장방식)
	 3. Setter를 사용한 의존성 주입
	 
	 */
	
	//생성자를 통한 의존성 주입 <== 권장하는 방식
		//Controller 에서 직접 Repository를 접근하지 않고 Service를 접근 하도록 함.
	//private final QuestionRepository questionrepository;	//Question..클래스 question..객체
	private final QuestionService questionService;
	
	private final UserService userService;
//	@GetMapping("/question/list") //http://localhost:9292/question/list
//	@PostMapping("/question/list") //Form 태그의 method=post action = "/question/list";
	//@ResponseBody		//요청을 브라우저에 출력 //question_list라고 요청했을때 String값이 브라우저에 출력
//	public String list(Model model) {
		//1. 클라이언트 요청 정보 : http://localhost:9292/question/list
		
		//2. 비즈니스 로직을 처리
//		List<Question> questionList = //questionList안에 Question의 정보가 다 들어가 있다.
				//this.questionrepository.findAll();	//직접 controller가 repository에 접근
//				this.questionService.getList();			// service에 접근해서 method를 호출
		//3. 뷰(view) 페이지로 전송
			//Model : 뷰페이지로 서버의 데이터를 담아서 전송 객체 ( Session, Application )
//		model.addAttribute("questionList", questionList);
		//model이란 객체를 만들고 addAttribute는 "questionList"에 questionList를 담아서 전송
//		return "question_list";	//list라는 메소드를 "question_list"로 String 값으로 리턴시켜준다
		
//	}
	
	//@2월 14일 페이징 처리를 위해 수정됨
	// http://localhost:9292/questioin/list/?page=0
	@GetMapping("/question/list")
	public String list(Model model, @RequestParam (value="page", defaultValue="0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
								//Param으로 변수가 value로 넘어오고, 아무것도 안 넘어오면 page변수에 0이 들어간다. 마지막에 int page로 변환
	
		
		//검색어에 해당하는 kw 파라미터를 추가했고 디폴트값으로 빈 문자열을 설정했다. 
		
	// 비즈니스 로직 처리 :
	Page<Question> paging =
		this.questionService.getList(page, kw);
		
		//model 객체에 결과로 받은 paging 객체를 client로 전송
		model.addAttribute("paging",paging);
		
		model.addAttribute("kw", kw);
		
	
		return "question_list";
	}
	
	
	
	
	//변수값이 들어오고 PathVariable이 처리한다. 들어오는 변수의 값을 Integer id 에 저장
	//상세 페이지를 처리하는 메소드 : /question/detail/1
	@GetMapping(value = "/question/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
		// 서비스 클래스의 메소드 호출 : 상세페이지 보여 달라
		
		Question q =
				this.questionService.getQuestion(id);
		
		//Model 객체에 담아서 클라이언트에게 전송
		model.addAttribute("question", q);
		return "question_detail";	//template : question_detail.html
	}
	
	@PreAuthorize("isAuthenticated()")
	//메소드이름이 같아도 매개변수가 다르면 다른 메소드로 인식한다.
	@GetMapping("/question/create")
	public String questionCreate(QuestionForm questionform) {
		return "question_form";
	}
	
	//@PreAuthorize("isAuthenticated()")이 붙은 메소드는 로그인이 필요한 메소드를
	//의미한다. 적용된 메소드가 로그아웃 상태에서 호출되면 로그인 페이지로 이동된다.
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/question/create")
	public String questionCreate(
			//@RequestParam String subject,@RequestParam String content
			@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal)
			{
				if (bindingResult.hasErrors()) {
					return "question_form";
				}
		
				SiteUser siteUser = this.userService.getUser(principal.getName());
				
				//로직 작성부분 (Service에서 로직을 만들어서 작동
		//this.questionService.create(subject, content, siteUser);		
				this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
				
				
		//값을 DB에 저장후 List 페이지로 리다이렉트 (질문 목록으로 이동)
		return "redirect:/question/list";
	}
	
	
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
 
    	Question question = this.questionService.getQuestion(id);
    	
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
        	
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            
        }
        
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        
        
        return "question_form";
    }
	
	
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, 
            Principal principal, @PathVariable("id") Integer id) {
    	
    	
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        
        Question question = this.questionService.getQuestion(id);
        
        
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        
        return String.format("redirect:/question/detail/%s", id);
    }
	
    
	@PreAuthorize("isAuthenticated()")
	
	@GetMapping("/question/delete/{id}")
	
	public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
		
		Question question = this.questionService.getQuestion(id);
		
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
			
		};
		
		this.questionService.delete(question);
		
		return "redirect:/";
		
	}
	
	@PreAuthorize("isAuthenticated()") //로그인한 사람만 추천 가능
	@GetMapping("/question/vote/{id}")
	 public String questionVote(Principal principal, @PathVariable("id") Integer id) {
	 Question question = this.questionService.getQuestion(id);
	 SiteUser siteUser = this.userService.getUser(principal.getName());
	 this.questionService.vote(question, siteUser);
	 return String.format("redirect:/question/detail/%s", id);
	 }
	
}
