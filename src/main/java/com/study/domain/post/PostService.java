package com.study.domain.post;

import com.study.common.dto.SearchDto;
import com.study.common.paging.Pagination;
import com.study.common.paging.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service //@Mapper와 유사 해당 클래스가 비즈니스 로직을 담당하는 서비스 레이어의 클래스임을 의미
@RequiredArgsConstructor //생성자로 빈을 주입,롬복에서 제공하는 기능으로 클래스내에 final로 선안된 모든 맴버에 대한 생성자를 만들어줌 -> 생성자 코드 작성이 필요가 없음
public class PostService {
    @Autowired
    private final PostMapper postMapper;

    /**
     * 게시글 저장
     *
     * @param params - 게시글 정보
     * @return Generated PK
     */
    @Transactional //선언적 트랜잭, 메서드의 실행과 동시에 트랜잭션이 시작, 메서드 정상 종료 여부에 따라 Commit 또는 Rollback 됨
    public Long savePost(final PostRequest params) {
        postMapper.save(params);
        return params.getId();
    }


    /**
     * 게시글 상세정보 조회
     *
     * @param id - PK
     * @return 게시글 상세정보
     */
    public PostResponse findPostById(final Long id) {
        return postMapper.findById(id);
    }


    /**
     * 게시글 수정
     *
     * @param params - 게시글 정보
     * @return PK
     */
    @Transactional
    public Long updatePost(final PostRequest params) {
        postMapper.update(params);
        return params.getId();
    }

    /**
     * 게시글 삭제
     *
     * @param id - PK
     * @return PK
     */
    public Long deletePost(final Long id) {
        postMapper.deleteById(id);
        return id;
    }

    /*
    조회수 추가
     */
    @Transactional
    public Long viewPlus(final Long id)
    {
        return postMapper.viewPlus(id);
    }



    public Long recomPlus(final Long id){
        postMapper.recomPlus(id);
        return id;
    }

    public Long unrecomPlus(final Long id){
        postMapper.unrecomPlus(id);
        return id;
    }





    /**
     * 게시글 리스트 조회
     *
     * @param params - search conditions
     * @return list & pagination information
     */
    public PagingResponse<PostResponse> findAllPost(final SearchDto params) {

        // 조건에 해당하는 데이터가 없는 경우, 응답 데이터에 비어있는 리스트와 null을 담아 반환
        int count = postMapper.count(params);
        if (count < 1) {
            return new PagingResponse<>(Collections.emptyList(), null);
        }

        // Pagination 객체를 생성해서 페이지 정보 계산 후 SearchDto 타입의 객체인 params에 계산된 페이지 정보 저장
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        // 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 데이터 조회 후 응답 데이터 반환
        List<PostResponse> list = postMapper.findAll(params);
        return new PagingResponse<>(list, pagination);
        //조회된 게시물 데이터와 계산된 페이지 정보를 담은 'list'와 'pagination'을 PagingResponse 클래스에 저장해서 호출
    }


}