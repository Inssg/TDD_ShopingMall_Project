package org.inssg.backend.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberApiTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 테스트")
    void 회원가입() {
        //given
        String email = "abc@gmail.com";
        String password = "test1234!";
        String username = "테스트1";

        MemberCreate memberCreate = new MemberCreate(email, password, username);
        //when
        memberService.createMember(memberCreate);
        //then
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException());

        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getUsername()).isEqualTo(username);
        assertThat(member.getPassword()).isEqualTo(password);

    }

}
