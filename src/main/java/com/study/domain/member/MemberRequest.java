package com.study.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //파라미터가 없는 기본 생성자 생성, 이 생성자는 PROTECTED 접근 제한자 가짐
public class MemberRequest {

    private Long id;                // 회원 번호 (PK)
    private String loginId;         // 로그인 ID
    private String password;        // 비밀번호
    private String name;            // 이름
    private Gender gender;          // 성별
    private LocalDate birthday;     // 생년월일

    public void encodingPassword(PasswordEncoder passwordEncoder) {
        if (StringUtils.isEmpty(password)) {
            return;
        }
        password = passwordEncoder.encode(password);
    }
    //패스워드 암호화 메소드(라이브러리)인 passwordEncoder을 인자로 받아 해당 비밀번호를 암호화(해싱=암호화하여 일방향으로 변환)한 뒤 passwrod에 저장.
    // 만약 비밀번호가 비어있을시 그냥 넘어감
}