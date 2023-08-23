package com.study.domain.post;

import com.study.common.dto.MessageDto;
import com.study.common.dto.SearchDto;
import com.study.common.paging.PagingResponse;
import com.study.domain.file.FileRequest;
import com.study.domain.file.FileResponse;
import com.study.domain.file.FileService;
import com.study.domain.file.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller //해당 클래스가 사용자의 요청과 응답을 처리하는 컨트롤러 클래스임을 명시, 컨트롤러는 뷰와 연결지어 작동함
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileService fileService;
    private final FileUtils fileUtils;

    // 게시글 작성 페이지
       @GetMapping("/post/write.do")
       /*
       과거에는 컨트롤러 메소드에 URL와 HTTP 요청 메소드를 매핑하기 위해 @RequestMapping을 이용해서 value에는 URI를, method에는 HTTP요청 메서드를 지정했어야만 했다
       하지만 스프링 4.3 버전 부터는 @GetMapping, @PostMapping등 요청 메소드의 타입별로 매핑을 처리할 수 있는 어노테이션이 추가됨
       */
    public String openPostWrite(@RequestParam(value = "id", required = false) final Long id, Model model) { //Model은 데이터를 화면(HRML)으로 전달할 때 사용됨
        /*
        @RequestParam은 화면에 보낸 파라미터를 전달받는데 사용됨, 신규 게시글 등록에는 id가 필요하지 않기 때문에 required를 false로 설정
        하지만 수정할 경우에는 id가 openPostWrite의 파라미터로 전송되고 id를 이용해 게시글 상세정보를 조회한 후 화면으로 전달함
          */
        if (id != null) { //id가 null이 아닐경우, 즉 게시글 수정일경우 조회한 게시글 상세 정보를 post라는 이름으로 화면으로 전달, write.html의 CDATA form action과 연관
            PostResponse post = postService.findPostById(id); //postService의 findPostById는 게시글 상세 정보 조회 메소드
            model.addAttribute("post", post); //데이터를 모델에 추가하여 웹의 뷰로 전달
            //뷰로 전달할 데이터를 모델에 추가하는 역할로 "post"는 키 값, post는 value값의 쌍으로 됨. 이후 밑에 return의 뷰(여기서는 HTML)에서 추가한 데이터 참조 가능
        }
        return "post/write"; //컨트롤러의 리턴은 일반적으로 사용자가 보는 화면(HTML)을, 처리할때는 리턴 타입을 String 으로 선언하고 리턴 문에 HTML 파일의 경로를 선언해주면됨
    }

      // 신규 게시글 생성
    @PostMapping("/post/save.do") //post방식인 이유는 저장한다는 동작이 들어가니까
    public String savePost(final PostRequest params, Model model) {
        Long id = postService.savePost(params); //PostRequest의 맴버변수를 가져와 사용자가 입력한 데이터 매핑, 즉 실제로 저장하는건 컨트롤러선에서 (서비스 레이어를 불러서) 처리하고 화면(뷰)는 컨트롤러로 입력한 데이터를 보내주는 것
        List<FileRequest> files = fileUtils.uploadFiles(params.getFiles()); //파일 데이터 디스크에 저장(업로드)
        fileService.saveFiles(id, files); //테이블에 저장
        MessageDto message = new MessageDto("게시글 생성이 완료되었습니다.", "/post/list.do", RequestMethod.GET, null);
        //MessageDto의 message라는 객체를 생성하고 리다이렉션으로 list.do로 이동하게 함, 방식은 get 방식이고, 추가적인 데이터는 없다
        return showMessageAndRedirect(message, model);
        //이 메서드에 메시지와 모델을 넣고 호출(맨 밑에 선언함)
    }

    // 게시글 리스트 페이지
    @GetMapping("/post/list.do") //GET방식의 HTTP 요청 메서드,데이터를 조회하거나 화면을 리턴하는 경우에 get 방식 이용
    public String openPostList(@ModelAttribute("params") final SearchDto params, Model model) {
        /*
        1:1로 매핑되는 단일 파라미터는 @RequestParam으로 전달받은 후 Model의 addAttribute()를 이용해서 뷰로 전달해야 하지만
        @ModelAttribute를 이용하면 파라미터로 수집한 객체를 자동으로 뷰까지 연결해줌
        여기서 params는 별칭으로 뷰에서는 "${params.page}"와 같이 객체에 접근이 가능함
        */
        PagingResponse<PostResponse> response = postService.findAllPost(params);
        //서비스 레이어의 findAllPost 메서드를 호출하여 페이징된 게시글 목록을 조회하고 PagingResponse<PostResponse> 타입의 response 변수에 할당한다
        //즉, response에는 조회된 게시글 목록과 해당 목록의 페이지 정보가 담아져 있다
        model.addAttribute("response", response); //"response"라는 이름으로 response 데이터를 화면으로 전달함
        return "post/list"; //리턴 타입이 String 이니까 리턴 문에 선언된 경로의 HTMl 파일이 화면에 출력됨
    }



     // 게시글 상세 페이지
     @GetMapping("/post/view.do")
     public String openPostView(@RequestParam final Long id, Model model) {
         PostResponse post = postService.findPostById(id);
         model.addAttribute("post", post); // 화면은 "${post.변수명}" 으로 응답 객체의 각 데이터에 접근
         postService.viewPlus(id); // 조회수 증가
         return "post/view";
     }

    @PostMapping("/post/recommend.do")
    public ResponseEntity<String> recommendPost(@RequestParam final Long id) {
        postService.recomPlus(id); // 추천 수 증가
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/post/unrecommend.do")
    public ResponseEntity<String> unrecommendPost(@RequestParam final Long id) {
        postService.unrecomPlus(id); // 추천 수 감소
        return ResponseEntity.ok("Success");
    }




    //게시글 정답 페이지
     @GetMapping("/post/answer.do")
    public String openPostAnswer(@RequestParam("id") Long id, Model model) {
        PostResponse post = postService.findPostById(id);
        model.addAttribute("post", post);
        return "post/answer";
    }

    //게시글 오답 페이지
     @GetMapping("/post/wrong.do")
    public String openPostWrong(@RequestParam("id") Long id, Model model) {
        PostResponse post = postService.findPostById(id);
        model.addAttribute("post", post);
        return "post/wrong";
    }



    //기존 게시글 수정
    @PostMapping("/post/update.do")
    public String updatePost(final PostRequest params, final SearchDto queryParams, Model model) {
        // 1. 게시글 정보 수정
        postService.updatePost(params);

        // 2. 파일 업로드 (to disk), 수정하는 시점에 새로 추가된 파일을 디스크에 업로드
        List<FileRequest> uploadFiles = fileUtils.uploadFiles(params.getFiles());

        // 3. 파일 정보 저장 (to database), 수정하는 시점에 새로 추가된 파일 정보를 DB에 저장
        fileService.saveFiles(params.getId(), uploadFiles);

        //4. 삭제할 파일 정보 조회 (from database), 수정하는 시점에 삭제된 파일 정보를 DB에서 조회
        List<FileResponse> deleteFiles = fileService.findAllFileByIds(params.getRemoveFileIds());

        //5. 파일 삭제 (from disk), 수정하는 시점에 삭제된 파일을 디스크에서 삭제
        fileUtils.deleteFiles(deleteFiles);

        //6. 파일 삭제 (from database), 수정하는 시점에 삭제된 파일을 DB에서 삭제 처리
        fileService.deleteAllFileByIds(params.getRemoveFileIds());

        MessageDto message = new MessageDto("게시글 수정이 완료되었습니다.", "/post/list.do", RequestMethod.GET, queryParamsToMap(queryParams));
        return showMessageAndRedirect(message, model);
    }


    // 게시글 삭제
    @PostMapping("/post/delete.do")
    public String deletePost(@RequestParam final Long id, final SearchDto queryParams, Model model) {
        postService.deletePost(id);
        MessageDto message = new MessageDto("게시글 삭제가 완료되었습니다.", "/post/list.do", RequestMethod.GET, queryParamsToMap(queryParams));
        return showMessageAndRedirect(message, model);
    }

    // 쿼리 스트링 파라미터를 Map에 담아 반환
    private Map<String, Object> queryParamsToMap(final SearchDto queryParams) {
        Map<String, Object> data = new HashMap<>();
        data.put("page", queryParams.getPage());
        data.put("recordSize", queryParams.getRecordSize());
        data.put("pageSize", queryParams.getPageSize());
        data.put("keyword", queryParams.getKeyword());
        data.put("searchType", queryParams.getSearchType());
        return data;
    }

    // 사용자에게 메시지를 전달하고, 페이지를 리다이렉트 한다.
    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params); //model객체에 "params"라는 이름으로 params객체(매개변수)의 데이터를 추가, 여기서 params는 위의 save의 messsage
        return "common/messageRedirect";
    }
}