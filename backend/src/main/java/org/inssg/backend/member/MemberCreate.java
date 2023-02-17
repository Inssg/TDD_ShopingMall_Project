package org.inssg.backend.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCreate {

    private String email;

    private String password;

    private String username;


    public MemberCreate(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
}
