package org.inssg.backend.security;

import net.bytebuddy.asm.MemberRemoval;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberNotFound;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.inssg.backend.security.userdetails.MemberDetailsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberDetailsServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberDetailsService memberDetailsService;


    @BeforeAll
    void setUp() {

        String email = "abc@gmail.com";
        String password = "1234";
        String username = "테스트";

        MemberCreate memberCreate = new MemberCreate(email, password, username);
        Member member = Member.create(memberCreate, passwordEncoder);
        memberRepository.save(member);

    }

    @Test
    @DisplayName("알맞은 email 전달시,MemberDetails 반환 ")
    void test_loadUserByUsername_returnsMemberDetails() {
        String email = "abc@gmail.com";
        MemberDetails memberDetails = (MemberDetails) memberDetailsService.loadUserByUsername(email);

        assertThat(memberDetails.getEmail()).isEqualTo(email);

    }

    @Test
    @DisplayName("틀린 email 전달시, 예외던짐")
    void test_loadUserByUsername_throwsException() {
        String email = "abc1234@gmail.com";

        assertThrows(MemberNotFound.class, () ->
                memberDetailsService.loadUserByUsername(email));
    }
}
