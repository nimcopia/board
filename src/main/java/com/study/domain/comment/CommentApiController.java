package com.study.domain.comment;

import com.study.common.paging.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //컨트롤러의 모든 메소드에 @ResponseBody 적용, 응답으로 페이지가 아닌 리턴 데이터 자체를 리턴함
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    // 신규 댓글 생성
    @PostMapping("/posts/{postId}/comments")
    public CommentResponse saveComment(@PathVariable final Long postId, @RequestBody final CommentRequest params) {
        //@PathVariable: REST API에서 리소스를 표현함, URL 경로에서 추춣할 값({postId})을 매개변수 (postId)에 할당함. 예를들어 URL 경로가 "posts/123/comments"이면 "123"이 postId에 할당됨
        /*
        @ReqeustBody:데이터를 생성 또는 수정하는 경우에 사용함 저장할 데이터를 JSON 포맷으로 해서 서버에 요청을 보내면 key-value 구조로 이루어진 각각의 데이터가 Java 객체와 매핑됨
        즉, 역할은 HTTP 요청의 본문에 포함된 데이터를 CommentRequest 객체로 변환하여 매개변수 params에 할당함
        예를들어 JSON 형태의 요청 본문 데이터가 {"id": 123, "content": "Hello"} 인 경우 해당 데이터가 CommentRequest의 id와 content에 저장됨
        */
        Long id = commentService.saveComment(params); //saveComment 메소드는 리턴으로 ID를 반환
        return commentService.findCommentById(id); //이전에 저장된 댓글의 ID를 사용하여 해당 댓글의 상세정보 조회
    } //결과적으로 이 메소드는 댓글을 생성하고 댓글의 상세정보(응답 객체)를 리턴함

    // 댓글 리스트 조회
    @GetMapping("/posts/{postId}/comments")
    public PagingResponse<CommentResponse> findAllComment(@PathVariable final Long postId, final CommentSearchDto params) {
        return commentService.findAllComment(params);
    }

    // 댓글 상세정보 조회
    @GetMapping("/posts/{postId}/comments/{id}")
    public CommentResponse findCommentById(@PathVariable final Long postId, @PathVariable final Long id) {
       //특정 게시글(postId)에 등록된 모든 댓글 중 PK(id)에 해당되는 댓글을 조회, 사용자에게 기존 댓글 정보를 보여줌
        return commentService.findCommentById(id);
    }


    // 기존 댓글 수정
    @PatchMapping("/posts/{postId}/comments/{id}") //이 엔드포인트에 요청이오면 호출되도록 설정함 엔드포인트가 위의 findCommentById와 같더라도 ajax의 type에 따라 호출되는게 다름
    public CommentResponse updateComment(@PathVariable final Long postId, @PathVariable final Long id, @RequestBody final CommentRequest params) {
        commentService.updateComment(params);  //특정 게시글(postId)에 등록된 모든 댓글 중 PK(id)에 해당되는 댓글을 수정함
        return commentService.findCommentById(id);
        //댓글 수정이 완료되면 수정된 댓글 정보를 리턴해줌, saveComment와 마찬가지로 @Requestbody를 이용해서 JSON 문자열로 넘어오는 댓글 정보를 CommentReuqest 객체의 각 맴버 변수에 매핑함
    }

    // 댓글 삭제
    @DeleteMapping("/posts/{postId}/comments/{id}")
    public Long deleteComment(@PathVariable final Long postId, @PathVariable final Long id) {
        return commentService.deleteComment(id); //특정 게시글에 모든 댓글 중 PK(id)에 해당되는 댓글을 삭제함.
    }

}