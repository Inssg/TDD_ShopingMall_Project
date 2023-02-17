package org.inssg.backend.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberApiTest {

    private MemberService memberService;

    @Test
    @DisplayName("회원가입 테스트")
    void 회원가입() {
        String email = "abc@gmail.com";
        String password = "test1234!";
        String username = "테스트1";

        MemberCreate request = new MemberCreate(email, password, username);

        memberService.createMember(request);
    }

    private class MemberCreate {
        private String email;
        private String password;
        private String username;

        public MemberCreate(String email, String password, String username) {
            this.email = email;
            this.password = password;
            this.username = username;
        }
    }

    private class MemberService {
        public void createMember(MemberCreate memberCreate) {

           Member member =  memberCreate.toEntity(passwordEncoder);
           memberRepository.save(member);
        }
    }

}
