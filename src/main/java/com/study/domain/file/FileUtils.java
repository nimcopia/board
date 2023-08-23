package com.study.domain.file;
/*
파일 업로드/다운로드를 모든 영역에서 공통으로 사용하기 위해 만든 클래스임.
이 클래스는 디스크에 디렉터리(폴더)를 생성하거나, 파일을 업로드 또는 삭제하는 용도
 */
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component //@Bean과 유사 차이점은 @Component는 개발자가 직접 정의한 클래스를 빈으로 등록할 때 사용
public class FileUtils {

    private final String uploadPath = Paths.get("C:","develop","upload-files").toString();
    /*
    물리적으로 파일을 저장할 위치를 의미, C:\develop \ upload-files를 기본위치로 선언함
    보통 OS별 디렉터리 경로를 구분할 때 FIle.separator을 이용하는데, Paths.get()을 이용하면 OS에 상관없이 디렉터리 경로 구분 가능
     */

    /*
        다중 파일 업로드 = 디스크에 생성된 파일 정보 담김
        @param multipartFiles : 파일 객체 List
        @retrun : DB에 저장할 파일 정보 List
     */
    public List<FileRequest> uploadFiles(final List<MultipartFile> multipartFiles){
        /*
        스프링이 제공하는 파일 업로드를 쉽게 하기 위한 MultipartFile 인터페이스, 사용자가 화면에서 파일을 업로드한 후 폼을
        전송하면, MultipartFile 객체에 사용자가 업로드한 파일 정보가 담김
         */
        List<FileRequest> files = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles){
            if (multipartFile.isEmpty()){
                continue;
            }
            files.add(uploadFile(multipartFile));
            //files에는 밑에 함수에서 리턴한 빌더 패턴이 적용된 객체의 디스크에 생성된 파일 정보가 담기게 된다
        }
        return files;
    }

    /*
        단일 파일 업로드
        @param multipartFile : 파일 객체
        @return : DB에 저장할 파일 정보
     */

    public FileRequest uploadFile(final MultipartFile multipartFile){
        if (multipartFile.isEmpty()){
            return null;
        }

        String saveName = generateSaveFilename(multipartFile.getOriginalFilename()); //저장할 파일명
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")).toString(); //오늘 날짜
        String uploadPath = getUploadPath(today) + File.separator + saveName; //업로드 경로 (디렉터리 + 파일명)
        File uploadFile = new File(uploadPath); //업로드할 파일 객체


        try{
            multipartFile.transferTo(uploadFile);
            //파일은 uploadPath에 해당되는 경로에 생성되며, MultipartFile의 transferTo()가 정상적으로 실행되면 파일 생성이 완료됨
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        return FileRequest.builder()        //리턴으로 디스크에 생성된 파일 정보가 담기게 됨
                .originalName(multipartFile.getOriginalFilename())
                .saveName(saveName)
                .size(multipartFile.getSize())
                .build();
    }

    /*
        저장 파일명 생성
        @param : filename 원본 파일명
        @return : 디스크에 저장할 파일명
     */

    private String generateSaveFilename(final String filename){
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        /*
            uuid.randomUUID().toString() 에는 32자리 랜덤 문자열을 저장, 또한 replaceAll해서 랜덤문자열중 하이픈을 제거한다
            extension에는 업로드 한 파일의 확장자를 담아
            최종적으로 리턴은 (랜덤 문자열 + "." + 파일 확장자)에 해당하는 파일명을 리턴함, 이 파일명은 실제로 디스크에 생성되는 파일명임
         */
        String extension = StringUtils.getFilenameExtension(filename); //확장자를 추출하는 메소드 .pdf 면 pdf를 반환하는식
        return uuid + "." + extension;
    }

    /*
        업로드 경로 반환
        @return : 업로드 경로
     */

    private String getUploadPath() {   // uploadPath에 해당되는 경로를 리턴함, 당장은 기본 업로드 경로에 오늘 날짜(today)를 연결
        return makeDirectories(uploadPath);
    }

    /*
        업로드 경로 반환
        @params addpath : 추가 경로
        @return : 업로드 경로
     */
    private String getUploadPath(final String addPath){ //uploadFile()의 변수 uploadPath에서 호출하는 메서드
        return makeDirectories(uploadPath + File.separator + addPath);
    }

    /*
        업로드 폴더(디렉터리) 생성
        @param path : ' - ' 가 들어가는 업로드 경로
        @return : 업로드 경로
     */
    private String makeDirectories(final String path){ //getUploadPath() 에서 호출하는 메서드
        //디스크에 경로(path)에 해당되는 디렉터리(폴더)가 없으면 path에 해당되는 모든 경로에 폴더 생성
        File dir = new File(path);
        if(dir.exists() == false){  //경로의 폴더 체크
            dir.mkdirs(); //없을시 폴더 생성
        }
        return dir.getPath();
    }

    /*
        파일 삭제(from disk), 파일 삭제전 밑 작업(폴더와 파일이름 전달)
        @parma files : 삭제할 파일 정보 List
     */
    public void deleteFiles(final List<FileResponse> files){
        if(CollectionUtils.isEmpty(files)){
            return;
        }
        for (FileResponse file : files){
            String uploadedDate = file.getCreatedDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
            //DB에 파일 정보가 저장된 시간(created_date)를 '연월일' 형식으로 포맷
            deleteFile(uploadedDate, file.getSaveName());
            //deleteFile에 연월일 폴더, 저장된 파일 이름 준다
        }
    }

    /*
        파일 삭제(from disk), 파일 삭제 전 마지막 작업(경로 설정)
        @param addPath : 추가경로
        @param filename : 파일명
        addpath는 deleteFiles의 uploadedDate로 업로드 연월일 폴더 처리,
     */
    private void deleteFile(final String addPath, final String filename){
        String filePath = Paths.get(uploadPath, addPath, filename).toString();
        //디스크에 저장된 파일의 전체경로(디렉터리 경로 + 파일명)를 filePath에 저장
        deletefile(filePath);
        //저장한 파일 전체 경로 삭제 로직 호출
    }

    /*
        파일 삭제 (from disk), 파일을 삭제하는 찐 로직
        @parma filePath : 파일 경로
     */
    private void deletefile(final String filePath){
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 다운로드할 첨부파일(리소스) 조회 (as Resource)
     * @param file - 첨부파일 상세정보
     * @return 첨부파일(리소스)
     */
    public Resource readFileAsResource(final FileResponse file){ //file : DB에서 조회한 첨부파일의 상세정보, 업로드된 첨부파일의 경로를 찾아냄
        String uploadedDate = file.getCreatedDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyMMdd")); //첨부파일이 업로드된 날짜
        String filename = file.getSaveName(); //디스크에 업로드된 파일명
        Path filePath = Paths.get(uploadPath, uploadedDate, filename); //디스크에 업로드된 파일 전체 경로
        try{
            Resource resource = new UrlResource(filePath.toUri()); //filePath에 담긴 첨부파일의 경로를 전달해서 Resource 객체를 생성
            if (resource.exists() == false || resource.isFile() == false){  //만약 리소스가 없거나, 리소스의 타입이 파일이 아닌 경우에는 예외를 던져 로직 종료

                throw new RuntimeException("file not found :"+filePath.toString());
            }
            return resource;
        } catch (MalformedURLException e){
            throw new RuntimeException("file not found : "+filePath.toString());
        }
    }


}
