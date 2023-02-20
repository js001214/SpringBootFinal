package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {

	private final AnswerRepository answerRepository; 
	
	//답변글으르 저장하는 메소드, Controller에서 Question 생성해서 아규먼트로 인풋
	public Answer create(Question question, String content, SiteUser author) {
			//매개변수로 question과 content 객체를 받는다.
		//Answer 객체를 생성후 아규먼트로 넘어오는 값을 setter 주입
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setAuthor(author);
		
		
		this.answerRepository.save(answer);
		
		return answer;
		
	}
	
	
	//답변 ID로 답변을 조회하는 메소드
	public Answer getAnswer(Integer id) {
		
		Optional<Answer> answer = this.answerRepository.findById(id);
		
		if (answer.isPresent()) {
			
			return answer.get();
		}else {
			
			throw new DataNotFoundException("answer not found");
		}
		
	}
	
	//답변의 내용으로 답변을 수정하는 메소드
	public void modify(Answer answer, String content) {
		
		answer.setContent(content);
		
		answer.setModifyDate(LocalDateTime.now());
		
		this.answerRepository.save(answer);
	}
	
	//삭제
	public void delete(Answer answer) {
		
		this.answerRepository.delete(answer);
	}
	
	public void vote(Answer answer, SiteUser siteUser) {
		
		answer.getVoter().add(siteUser);
		
		this.answerRepository.save(answer);
	}
	
}
