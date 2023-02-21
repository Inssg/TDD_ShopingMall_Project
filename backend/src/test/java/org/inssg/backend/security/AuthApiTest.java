package org.inssg.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.dto.LoginDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    @DisplayName("로그인 성공")
    void test_login_success() throws Exception {

        ResultActions actions = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 성공시, 토큰 반환")
    void test_login_returnsTokenResponse() throws Exception {

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(header().string("Authorization", notNullValue()))
                .andExpect(header().string("RefreshToken", notNullValue()));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 계정")
    void test_login_NotExistEmail() throws Exception {

        LoginDto wrongLoginDto = LoginDto.builder().username("abc123@gmail.com").password("1234").build();

        ResultActions actions = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongLoginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디나 비밀번호가 맞지 않습니다. 다시 확인해 주십십오."));
    }

    @Test
    @DisplayName("로그인 실패 - 패스워드 불일치")
    void test_login_WrongPassword() throws Exception {

        LoginDto wrongLoginDto = LoginDto.builder().username("abc@gmail.com").password("1234!!#@").build();

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(wrongLoginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디나 비밀번호가 맞지 않습니다. 다시 확인해 주십십오."));
    }


}
