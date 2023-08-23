package com.study.common.paging;

import com.study.common.dto.SearchDto;
import lombok.Getter;

@Getter
public class Pagination {

    private int totalRecordCount;     // 전체 데이터 수
    //COUNT(*) 쿼리의 실행 결과임, 즉 전체 게시글 개수를 의미함
    private int totalPageCount;       // 전체 페이지 수
    private int startPage;            // 첫 페이지 번호
    private int endPage;              // 끝 페이지 번호
    private int limitStart;           // LIMIT 시작 위치
    //LIMIT 구문에 사용되는 offset과 동일한 기능을 하는 변수임, offset을 대신해서 LIMIT 구문의 첫 번째 인자로 사용됨
    private boolean existPrevPage;    // 이전 페이지 존재 여부
    //startPage가 1이 아니라면 이전 페이지가 존재함
    private boolean existNextPage;    // 다음 페이지 존재 여부
    //recordSize(페이지당 출력할 개수)와 endPage(끝 페이지 번호)를 곱하면 한 화면 페이지(목차를 안 넘어간 화면)의 개수가 나오는데
    //만약 10*10으로 100이 나왔지만 totalRecordCount(전체 데이터 개수)가 105개라면 다음 페이지(목차)가 존재한다는 의미이다.
    public Pagination(int totalRecordCount, SearchDto params) {
        if (totalRecordCount > 0) {
            this.totalRecordCount = totalRecordCount;
            calculation(params);
            params.setPagination(this); //params에다 계산된 페이지 정보를 저장함
        }
    }

    private void calculation(SearchDto params) {

        // 전체 페이지 수 계산
        totalPageCount = ((totalRecordCount - 1) / params.getRecordSize()) + 1; //getRecordSize(): 페이지당 출력할 글 수
        //전체 데이터 수 - 페이지당 출력할 개수

        // 현재 페이지 번호가 전체 페이지 수보다 큰 경우, 현재 페이지 번호에 전체 페이지 수 저장
        if (params.getPage() > totalPageCount) { //getPage()는 현재 페이지 번호로 롬복 Getter 문법
            params.setPage(totalPageCount);
        }

        // 첫 페이지 번호 계산
        startPage = ((params.getPage() - 1) / params.getPageSize()) * params.getPageSize() + 1; //getPageSize(): 페이지당 항목 수 즉 ,화면 하단 페이지 수
        //무조건 getPageSize * ? + 1 의 값 밖에 안 나온다. 즉 getPageSize의 배수로 맨 처음을 정할 수 있음
        // -1하는 이유는 만약 -1 하지 않으면 현재 페이지 번호가 20이고 페이지 사이즈가 10이면 20/10 = 2로 '(2*10)+1' ->스타트 위치가 21이 된다

        // 끝 페이지 번호 계산
        endPage = startPage + params.getPageSize() - 1; //위에서 구한 시작 페이지 번호에 화면 하단 페이지 개수를 더하고 -1
        //스타트 페이지가 11이고 페이지당 항목 수가 10이면 -1하지 않으면 21이 되기 떄문에 -1을 한다

        // 끝 페이지가 전체 페이지 수보다 큰 경우, 끝 페이지 전체 페이지 수 저장, 언더플로 방지 같은 느낌
        if (endPage > totalPageCount) {
            endPage = totalPageCount;
        }

        // LIMIT(특정 범위의 데이터 조회) 시작 위치 계산
        limitStart = (params.getPage() - 1) * params.getRecordSize();
        //조회할 데이터의 시작위치가 저장됨
        //예를들어 현재 페이지 번호가 3이면 21번째의 데이터부터 조회하겠다는의미

        // 이전 페이지 존재 여부 확인
        existPrevPage = startPage != 1;

        // 다음 페이지 존재 여부 확인
        existNextPage = (endPage * params.getRecordSize()) < totalRecordCount;
    }

}