package com.study.domain.file;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FileRequest {

    private Long id;
    private Long postId;
    private String originalName;
    private String saveName;
    private long size;

    @Builder //롬복에서 제공, 빌더 패턴으로 객체를 생성가능하게함
    //장점은 1.생성자 파라미터가 많을 경우 가독성 상승, 2.어떤 값을 먼저 설정하던 상관없음
    public FileRequest(String originalName, String saveName, long size){
        this.originalName = originalName;
        this.saveName = saveName;
        this.size = size;
    }

    public void setPostId(Long postId){
        this.postId = postId;
    }

}
