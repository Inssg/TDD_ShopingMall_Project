package org.inssg.backend.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCreate {

    @Email(message = "이메일 형식으로 입력해 주세요.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "닉네임을 설정해주세요.")
    private String username;

    @Builder
    public MemberCreate(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
}
