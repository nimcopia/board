package com.study.config;

import com.study.interceptor.LoggerInterceptor;
import com.study.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override //인터셉터 폴더의 2가지 클래스 import함
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor()) //LoggerInterceptor 객체를 생성하여 인터셉터 추가
                .excludePathPatterns("/css/**", "/images/**", "/js/**"); //LoggerInterceptor은 css, images, js 에는 적용 x

        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/**/*.do") //LoginCheckInterceptor은  ~.do로 끝나는 경로에는 적용
                .excludePathPatterns("/log*"); //로그인,로그아웃 관련 URI 호출때는 인터셉트 중지
    }

}