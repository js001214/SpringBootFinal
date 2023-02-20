package com.mysite.sbb;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;


//@Component를 사용해서 CommonUtil 클래스를 생성. CommonUtil 클래스는  빈(bean, 자바객체)로 등록된다.
@Component
public class CommonUtil {
	public String markdown(String markdown) {
		Parser parser = Parser.builder().build();
		
		Node document = parser.parse(markdown);
		
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		
		return renderer.render(document);
	}
}