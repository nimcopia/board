package com.study.domain.post;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private Long id;                       // PK
    private String title;                  // 제목
    private String content;                // 내용
    private String content2;
    private String content3;
    private String content4;
    private String problem;
    private String writer;                 // 작성자
    private String exp;
    private String ansnum;
    private int viewCnt;                   // 조회 수
    private int recomCnt;                   // 추천 수
    private Boolean noticeYn;              // 공지글 여부
    private Boolean deleteYn;              // 삭제 여부
    private LocalDateTime createdDate;     // 생성일시
    private LocalDateTime modifiedDate;    // 최종 수정일시



}