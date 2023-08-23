package com.study.domain.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor //final 맴버에 대해 생성자 생성과 자동 빈 주입
public class FileService {

    private final FileMapper fileMapper;

    @Transactional
    public void saveFiles(final Long postId, final List<FileRequest> files){ //List는 컬렉션 프레임워크 라이브러리임
        if (CollectionUtils.isEmpty(files)){
            //CollectionUtils : 컬렉션 라이브러리에 포함된 유틸리티 클래스 addAll(), containsAny(), intersection(), isEmpty() 메서드들을 주로 사용함
            return;
        }

        for (FileRequest file : files){
            file.setPostId(postId); //모든 요청 객체에 게시글 번호를 세팅
        }
        /*
        향상된 for문 바꾼거
        for (int i = 0; i < files.size(); i++) {
                FileRequest file = files.get(i);
                file.setPostId(postId);
            }
         */
        fileMapper.saveAll(files); //테이블 파일 정보 저장
    }

    /*
        파일 리스트 조회
        @param postId : 게시글 번호 (fk)
        @return : 파일 리스트
     */
    public List<FileResponse> findAllFileByPostId(final Long postId){
        return fileMapper.findAllByPostId(postId);
    }

    /*
        파일 리스트 조회
        @param ids : pk 리스트
        @return : 파일 리스트
     */
    public List<FileResponse> findAllFileByIds(final List <Long> ids){
        if (CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        return fileMapper.findAllByIds(ids); //findAllByIds는 ids가 비어있을 때 사이즈가 0인 비어있는 리스트 리턴
    }

    /*
        파일 삭제
        @param ids : pk 리스트
     */
    @Transactional
    public void deleteAllFileByIds (final List<Long> ids){
        if (CollectionUtils.isEmpty(ids)){
            return;
        }
        fileMapper.deleteAllByIds(ids);
    }

    /**
     * 파일 상세정보 조회
     * @param id - PK
     * @return 파일 상세정보
     */
    public FileResponse findFileById(final Long id) {
        return fileMapper.findById(id);
    }

}
