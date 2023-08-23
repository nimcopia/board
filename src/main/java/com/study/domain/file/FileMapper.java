package com.study.domain.file;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FileMapper {

    //파일 정보 저장, @param files = 파일 정보 리스트, 업로드 된 파일 정보를 DB에 저장, 여러 개의 파일 받기 위해 List로 설정
    void saveAll(List<FileRequest> files);

    /*
        파일 리스트 조회
        @param postId : 게시글 번호 (fk)
        @return : 파일 리스트
     */
    List<FileResponse> findAllByPostId(Long postId); //게시글 번호 기준으로 게시글에 등록된 모든 첨부파일 조회

    /*
        파일 리스트 조회
        @param ids : pk 리스트
        @return : 파일 리스트
     */
    List<FileResponse> findAllByIds(List<Long> ids); //리스트 타입의 파일 번호(ids)를 기준으로 여러 개의 첨부파일 조회

    /*
        파일 삭제
        @param ids : PK 리스트
     */
    void deleteAllByIds(List<Long> ids); //리스트 타입의 파일 번호(ids)를 기준으로 DB에서 첨무파일 삭제

    /**
     * 파일 상세정보 조회
     * @param id - PK
     * @return 파일 상세정보
     */
    FileResponse findById(Long id);

}
