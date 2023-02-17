package org.inssg.backend.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberApiTest {

    @Test
    @DisplayName("회원가입 테스트")
    void 회원가입() {
        String email = "abc@gmail.com";
        String password = "test1234!";
        String username = "테스트1";
        MemberCreate request = new MemberCreate(email, password, username);

        memberService.createMember(request);
    }


}
