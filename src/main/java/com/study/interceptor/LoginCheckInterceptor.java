package com.study.interceptor;

import com.study.domain.member.MemberResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override //HandlerInterceptor 인터페이스 오버라이딩함 (3가지의 메소드가 있는데 그중 preHandle을 써먹는다)
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    //이 preHandel 메소드는 컨트롤러가 요청을 처리하기 전에 실행되는 메소드로 인터셉터에서 추가적인 동작을 수행하고자 할 때 사용함
        // 1. 세션에서 회원 정보 조회
        HttpSession session = request.getSession();
        MemberResponse member = (MemberResponse) session.getAttribute("loginMember");
        //loginMember라는 이름으로 저장된 회원 정보를 가져옴 -> 컨트롤러에서 저 이름으로 저장함

        // 2. 회원 정보 체크, 이걸 통해 로그인이 되어있지 않은 사용자 또한 URL을 입력해 강제로 게시판에 접근하는 사용자 차단 가능
        if (member == null || member.getDeleteYn() == true) {
            response.sendRedirect("/login.do");
            return false;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
        //preHandle 메소드 호출 코드 현재 인터셉터에서 다음 인터셉터로 요청을 전달하기 위해 작성
    }

}