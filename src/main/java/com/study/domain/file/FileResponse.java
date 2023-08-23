package com.study.domain.file; //DB에 저장된 파일 정보를 조회하는 용도의 클래스임


import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
public class FileResponse {

    private Long id; //파일 번호 (pk)
    private Long postId; //게시글 번호 (fk)
    private String originalName; //원본 파일명
    private String saveName; //저장 파일명
    private long size; //파일 크기
    private Boolean deleteYn;
    private LocalDateTime createdDate;
    private LocalDateTime deletedDate;
}

