package com.study.domain.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostRequest {

    private Long id;             // PK
    private String title;        // 제목
    private String content;      // 내용
    private String content2;
    private String content3;
    private String content4;
    private String problem;
    private String writer;       // 작성자
    private String exp;
    private String ansnum;
    private Boolean noticeYn;    // 공지글 여부
    private int viewCnt;                   // 조회 수
    private int recomCnt;                   // 추천 수
    private List<MultipartFile> files = new ArrayList<>();
    //MultipartFile은 스프링의 파일 업로드 처리 인터페이스 HTTP 요청에서 전송된 파일의 정보와 내용을 다룸
    private List<Long> removeFileIds = new ArrayList<>(); //삭제할 첨부파일 id List


}