package org.inssg.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper mapper;

    LoginDto loginDto = LoginDto.builder()
            .username("abc@gmail.com")
            .password("1234")
            .build();

    @Test
    @DisplayName("로그인 성공")
    void test_login_success() throws Exception {


        String email = "abc@gmail.com";
        String password = "1234";
        String username = "테스트1";
        MemberCreate memberCreate = new MemberCreate(email, password, username);
        Member member = Member.create(memberCreate, passwordEncoder);
        memberRepository.save(member);


        ResultActions actions = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 email")
    void test_login_NotExistEmail() throws Exception {

        ResultActions actions = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect((result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(NullPointerException.class))));
    }
}
