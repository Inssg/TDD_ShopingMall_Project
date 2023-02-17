package org.inssg.backend.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import sun.security.krb5.internal.ccache.MemoryCredentialsCache;

public class MemberApiTest {

    private MemberService memberService;
    private PasswordEncoder passwordEncoder;
    private MemberRepository memberRepository;


    @Test
    @DisplayName("회원가입 테스트")
    void 회원가입() {
        String email = "abc@gmail.com";
        String password = "test1234!";
        String username = "테스트1";

        MemberCreate memberCreate = new MemberCreate(email, password, username);

        memberService.createMember(memberCreate);
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
           Member member =  Member.create(memberCreate,passwordEncoder);
           memberRepository.save(member);
        }
    }

    public class Member {
        private Long id;
        private String email;
        private String password;
        private String username;

        public Member(String email, String password, String username) {
            this.email = email;
            this.password = password;
            this.username = username;
        }

        public Member create(MemberCreate memberCreate, PasswordEncoder passwordEncoder) {
            return new Member(memberCreate.email, passwordEncoder.encode(memberCreate.password), memberCreate.username);
        }
    }

    private class MemberRepository {
    }
}
