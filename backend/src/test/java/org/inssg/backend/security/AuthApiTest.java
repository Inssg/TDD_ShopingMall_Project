package org.inssg.backend.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.dto.LoginDto;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.redis.RedisService;
import org.inssg.backend.security.service.AuthService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper mapper;

    LoginDto loginDto = LoginDto.builder()
            .username("abc@gmail.com")
            .password("1234")
            .build();

    String accessToken;
    String refreshToken;

    @BeforeAll
    void setUp() {

        String email = "abc@gmail.com";
        String password = "1234";
        String username = "테스트";

        MemberCreate memberCreate = new MemberCreate(email, password, username);
        Member member = Member.create(memberCreate, passwordEncoder);
        memberRepository.save(member);

        accessToken = jwtTokenProvider.createAccessToken(member);
        refreshToken = jwtTokenProvider.createRefreshToken(member);

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

    @Test
    @DisplayName("로그인 성공시, Redis에 refreshToken 저장")
    void test_login_storeRefreshTokenInRedis() throws Exception {

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());

        assertThat(redisTemplate.opsForValue().get(loginDto.getUsername())).isNotNull();

    }
    //Todo: 시큐리티 RestDocs 작업
    @Test
    @DisplayName("로그아웃 테스트")
    void test_logout() throws Exception {
        //given
        doNothing().when(authService).logout(Mockito.anyString(), Mockito.anyString());

                mockMvc.perform(post("/member/logout")
                        .header("Authorization", "Bearer" + accessToken)
                        .header("RefreshToken", refreshToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 재발급 테스트")



}
