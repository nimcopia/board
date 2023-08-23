package com.study.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

//이 클래스는 alert 메시지 DTO(데이터 전송 객체) 이다.
@Getter
@AllArgsConstructor //롬복에서 제공해줌, 이 어노테이션이 선언된 클래스는 전체 맴버 변수를 필요로 하는 생성자가 생성됨
public class MessageDto {

    private String message;              // 사용자에게 전달할 메시지
    private String redirectUri;          // 리다이렉트 URI
    private RequestMethod method;        // HTTP 요청 메서드, RequestMethod는 스프링 web 라이브러리에 포함된 enum(상수 처리용) 클래스
    private Map<String, Object> data;    // 화면(View)으로 전달할 데이터(파라미터), 페이지별로 전달할 파라미터의 개수는 랜덤하기 때문에 여러 데이터를 key - value 형태로 담을 수 있는 Map을 이용함



}