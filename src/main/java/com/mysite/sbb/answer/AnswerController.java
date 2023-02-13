package com.mysite.sbb.answer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/answer") //postmapping이나 getmapping 앞에 자동으로 /answer이 들어간다.
@RequiredArgsConstructor
@Controller
public class AnswerController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	
	//http://localhost:9292/answer/create/1 요청에 대한 답변글 등록
	
	@PostMapping("/create/{id}")
	public String createAnswer(
			//<Validation 전 구성>
			//Model model, @PathVariable("id") Integer id, @RequestParam String content
			
			//content의 유효성 검사
			Model model, @PathVariable("id") Integer id,
			@Valid AnswerForm answerForm, BindingResult bindingResult
			) {
		
		Question question = this.questionService.getQuestion(id);
		
		//content의 값이 비어 있을때 
		if (bindingResult.hasErrors()){ 
			model.addAttribute("question", question);
			return "question_detail";
		}		
		
		//답변 내용을 저장하는 메소드 호출 (Service에서 호출)
		
		this.answerService.create(question, answerForm.getContent());
		
		//requestparam을쓰고  question_detail에 있는 id,name="content"를 받아온다.
		return String .format("redirect:/question/detail/%s", id);
							//redirect는 클라이언트가 서버에 새롭게 요청하는 것이다.
	}
	
}
