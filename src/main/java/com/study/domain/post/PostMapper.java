package com.study.domain.post;

import com.study.common.dto.SearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List; //컬렉션 프레임워크 인터페이스, 요소들을 인덱스로 접근하고 중복된 요소를 허용. 배열과 유사하지만 동적 크기 조정과 다양한 메소드 활용가능

@Mapper
public interface PostMapper {

    /**
     * 게시글 저장
     * @param params - 게시글 정보
     */
    void save(PostRequest params); //게시글 생성하는 INSERT(받은 값을 집어 넣으니까 create가 아님) 쿼리 호출

    /**
     * 게시글 상세정보 조회
     * @param id - PK
     * @return 게시글 상세정보
     */
    PostResponse findById(Long id); //select 쿼리 호출, 파라미터로 응답클래스의 id를 전달받아 쿼리의 where 조건으로 사용, 쿼리가 실행되면 결괏값이 응답 클래스 객체의 각 맴버 변수에 매핑됨


    /**
     * 게시글 수정
     * @param params - 게시글 정보
     */
    void update(PostRequest params); //UPDATE 쿼리 호출

    /**
     * 게시글 삭제
     * @param id - PK
     */
    void deleteById(Long id); //UPDATE 쿼리 호출 delete_yn을 (0) 에서 (1)로 수정

    /*
        조회수 증가
     */
    Long viewPlus(Long id);

    int getRecommendCount(@Param("postId") Long postId);

    Long recomPlus(Long id);

    Long unrecomPlus(Long id);



    /**
     * 게시글 리스트 조회
     * @return 게시글 리스트
     */
    List<PostResponse> findAll(SearchDto params); //여러개의 게시글을 리스트에 담아서 보여줌


    /**
     * 게시글 수 카운팅
     * @return 게시글 수
     */
    int count(SearchDto params); //페이징에 쓰임


}