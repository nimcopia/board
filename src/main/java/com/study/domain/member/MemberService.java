package com.study.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder; //SecurityConfig에서 선언한 PasswordEncoder 빈

    /**
     * 회원 정보 저장 (회원가입)
     * @param params - 회원 정보
     * @return PK
     */
    @Transactional
    public Long saveMember(final MemberRequest params) {
        params.encodingPassword(passwordEncoder); //encodingPassword를 호출해 비밀번호 암호화 ->60자리의 난수로 리턴함
        memberMapper.save(params);
        return params.getId();
    }

    /**
     * 회원 상세정보 조회
     * @param loginId - UK
     * @return 회원 상세정보
     */
    public MemberResponse findMemberByLoginId(final String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    /**
     * 회원 정보 수정
     * @param params - 회원 정보
     * @return PK
     */
    @Transactional
    public Long updateMember(final MemberRequest params) {
        params.encodingPassword(passwordEncoder);
        memberMapper.update(params);
        return params.getId();
    }

    /**
     * 회원 정보 삭제 (회원 탈퇴)
     * @param id - PK
     * @return PK
     */
    @Transactional
    public Long deleteMemberById(final Long id) {
        memberMapper.deleteById(id);
        return id;
    }


    /**
     * 회원 수 카운팅 (ID 중복 체크)
     * @param loginId - UK
     * @return 회원 수
     */
    public int countMemberByLoginId(final String loginId) {
        return memberMapper.countByLoginId(loginId);
    }
    //로그인 ID를 기준으로 회원 수를 카운팅

    /**
     * 로그인
     * @param loginId - 로그인 ID
     * @param password - 비밀번호
     * @return 회원 상세정보
     */
    public MemberResponse login(final String loginId, final String password) {

        // 1. 회원 정보 및 비밀번호 조회
        MemberResponse member = findMemberByLoginId(loginId); //loginId값 가져오는 함수(위에 정의됨)
        String encodedPassword = (member == null) ? "" : member.getPassword(); //회원정보가 없는 경우 빈 문자열로 초기화 getPassword()메소드는 어디에 정의되있는지 모르겠다 ->response 클래스 password 변수 @Getter 적용

        // 2. 회원 정보 및 비밀번호 체크
        if (member == null || passwordEncoder.matches(password, encodedPassword) == false) { //passwordEncoder.matches는 Spring Security에서 제공하는 메소드, 주어진 비밀번호와 암호화된 비밀번호를 비교함 맞으면 true 틀리면 false
            return null;
        }

        // 3. 회원 응답 객체에서 비밀번호를 제거한 후 회원 정보 리턴
        member.clearPassword();
        return member;
    }
}