package com.study.domain.comment;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper //DB와 통신하는 인터페이스임을 의미함 XML Mapper에서 메서드명과 동일한 ID를 가진 SQL 쿼리를 찾아 실행
public interface CommentMapper {

    /**
     * 댓글 저장
     * @param params - 댓글 정보
     */
    void save(CommentRequest params);

    /**
     * 댓글 상세정보 조회
     * @param id - PK
     * @return 댓글 상세정보
     */
    CommentResponse findById(Long id);//id를 기준으로 특정 댓글의 상세정보 조회
    //쿼리가 실행되면 응답 클래스 객체의 각 맴버 변수에 결과값이 매핑됨

    /**
     * 댓글 수정
     * @param params - 댓글 정보
     */
    void update(CommentRequest params);

    /**
     * 댓글 삭제
     * @param id - PK
     */
    void deleteById(Long id);

    /**
     * 댓글 리스트 조회
     * @param params - search conditions
     * @return 댓글 리스트
     */
    List<CommentResponse> findAll(CommentSearchDto params); //응답 클래스 객체들의 리스트를 반환함
    //추후 DB나 외부 소스에서 댓글 데이터를 가져와 CommentResponse 객체로 변환한 후 리스트에 추가하여 반환함
    //findAll 메소드를 호출한 곳에서는 List<CommentResponse>를 통해 조회된 뎃글 데이터를 이용 가능함

    /**
     * 댓글 수 카운팅
     * @param params - search conditions
     * @return 댓글 수
     */
    int count(CommentSearchDto params);

}
