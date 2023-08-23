package com.study.domain.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@RestController //이 컨트롤러의 모든 메서드에는 자동으로 @ResponseBody(화면이 아닌 리턴 타입에 해당하는 데이터 자체를 리턴)가 적용됨
@RequiredArgsConstructor
public class FileApiController {

    private final FileService fileService;
    private final FileUtils fileUtils;

    @GetMapping("/posts/{postId}/files")//URL에서 받아오는 값에 따라 postId의 값이 변함, @Pathvariable은 이 경로변수 '{postId}'를 메서드의 매개변수 postId에 바인딩해줌
    public List<FileResponse> findAllFileByPostId(@PathVariable final Long postId){
        return fileService.findAllFileByPostId(postId);
    }

    @GetMapping("/posts/{postId}/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable final Long postId, @PathVariable final Long fileId) {
        FileResponse file = fileService.findFileById(fileId); //DB에서 조회한 첨부파일 상세정보
        Resource resource = fileUtils.readFileAsResource(file); //read..()에 첨부파일 상세정보를 전달해 다운로드할 첨부파일을 리소스 타입으로 조회
        try{
            String filename = URLEncoder.encode(file.getOriginalName(), "UTF-8");//다운로드할 첨부파일 이름
            //리소스를 읽어들일 떈 실제로 디스크에 저장된 파일명(saveName)으로 접근했지만 다운로드되는 첨부파일은 원본 파일명(originalName)이 되야함
            //URLEncoder.encode()를 이용해 첨부파일의 이름이 깨지는것을 방지함

            /*
                 ResponseEntity는 RestApi 방식에서 사용되는 클래스로 사용자의 HTTP 요청에 대한 응답 데이터를 개발자가 직접 제어 가능
                 ResponseEntity.ok()는 HTTP 응답 상태 코드 200(ok)를 반환하는 메서드임, 파일 다운로드 요청이 성공적으로 처리됬다는 뜻
                 밑에 .으로 되있는 것들은 이제 응답에 대한 설정이다
                 .contentType은 응답의 컨텐츠 타입을 MediaType.APPLICATION_OCTET_STREAM으로 설정하는데 이진 파일을 나타내는 MIME 타입임
                 .header 중 위에것은 응답 헤더에 파일 다운로드에 필요한 정보를 추가한다 CONTENT_DISPOSITION 헤더는 첨부 파일의 다운로드를
                    지시하는데 사용됨, attachment 값은 다운로드를 위한 첨부 파일임을 나타냄, filename은 다운로드되는 파일의 이름을 지정함
                 아래것은 응답 헤더에 파일의 크기를 추가한다 CONTENT_LENGHTH는 응답 컨텐츠의 크기를 나타냄, 그것에 관한 value로 파일의 크기를 가져오는 file.getSize()
                 .body는 응답의 본문(body)에 파일 리소스를 설정한다, 이를 통해 클라이언트는 파일을 다운로드 가능
             */
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + filename + "\";")
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "")
                    .body(resource);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("filename encoding failed : " + file.getOriginalName());
        }
    }

}

