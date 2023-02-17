package com.mysite.sbb.answer;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/answer") //postmapping이나 getmapping 앞에 자동으로 /answer이 들어간다.
@RequiredArgsConstructor
@Controller
public class AnswerController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UserService userService;
	
	//http://localhost:9292/answer/create/1 요청에 대한 답변글 등록
	@PreAuthorize("isAu"
			+ "thenticated()")
	
	@PostMapping("/create/{id}")
	public String createAnswer(
			//<Validation 전 구성>
			//Model model, @PathVariable("id") Integer id, @RequestParam String content
			
			//content의 유효성 검사
			Model model, @PathVariable("id") Integer id,
			@Valid AnswerForm answerForm, BindingResult bindingResult,
			Principal principal)	//로그인한 사용자에 대한 정보를 알기 위해서는 Principal 객체를 사용해야 한다.
			 {						//principal.getName()을 호출하면 현재 로그인한 사용자의 사용자명(ID)을 알수있다.
		
		Question question = this.questionService.getQuestion(id);
		
		SiteUser siteUser = this.userService.getUser(principal.getName());
		//로그온한 사용자명을getUser을 통해 가져와서 siteUser객체에 담는다.
		
		
		//content의 값이 비어 있을때 
		if (bindingResult.hasErrors()){ 
			model.addAttribute("question", question);
			return "question_detail";
		}		
		
		//답변 내용을 저장하는 메소드 호출 (Service에서 호출)
		this.answerService.create(question, answerForm.getContent(), siteUser);
		
		//requestparam을쓰고  question_detail에 있는 id,name="content"를 받아온다.
		return String .format("redirect:/question/detail/%s", id);
							//redirect는 클라이언트가 서버에 새롭게 요청하는 것이다.
	}
	
	
	//URL의 답변 아이디를 통해 조회한 답변 데이터의 "내용"을 AnswerForm 객체에 대입하여
	//answer_form.html 템플릿에서 사용할수 있도록 했다.
	@PreAuthorize("isAuthenticated()")
	
	@GetMapping("/modify/{id}")
	
	public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
		
		Answer answer = this.answerService.getAnswer(id);
		
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
			
		}
		
		answerForm.setContent(answer.getContent());
		
		return "answer_form";
		
	}
	
	@PreAuthorize("isAuthenticated()")
	
	@PostMapping("/modify/{id}")
	
	public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
			
			@PathVariable("id") Integer id, Principal principal) {
		
		if(bindingResult.hasErrors()) {
			
			return "answer_form";
		}
		
		Answer answer = this.answerService.getAnswer(id);
		
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
			
		}
		
		this.answerService.modify(answer, answerForm.getContent());
		
		return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
		
		}
		
	}
			
			
	
	
	

