package org.inssg.backend.security.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginDto {

    @Email
    @NotBlank(message = "아이디는 공백이 아니어야 합니다.")
    private String userName;

    @NotBlank(message = "비밀먼호는 공백이 아니어야 합니다.")
    private String password;

    @Builder
    public LoginDto(String username, String password) {
        this.userName = username;
        this.password = password;
    }
}
