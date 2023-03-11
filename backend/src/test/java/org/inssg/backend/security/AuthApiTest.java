package org.inssg.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.dto.LoginDto;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.redis.RedisService;
import org.inssg.backend.security.service.AuthService;
import org.inssg.backend.util.AcceptanceTest;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.inssg.backend.util.ApiDocumentUtils.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthApiTest extends AcceptanceTest {

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

    @Autowired
    private RedisService redisService;

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

    @AfterEach
    void setup() {
        redisService.deleteValues("abc@gmail.com");
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

        //given


        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpect(header().string("Authorization", notNullValue()))
                .andExpect(header().string("RefreshToken", notNullValue()))
                .andDo(document(
                        "login-returnToken",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userName").type(JsonFieldType.STRING).description("회원 이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
                                )
                        ),
                        responseHeaders(
                                List.of(
                                        headerWithName("Authorization").description("Access Token"),
                                        headerWithName("RefreshToken").description("Refresh Token"))
                        )
                ));
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

        assertThat(redisTemplate.opsForValue().get(loginDto.getUserName())).isNotNull();

    }

    @Test
    @DisplayName("로그아웃 테스트")
    void test_logout() throws Exception {
        //given
        doNothing().when(authService).logout(Mockito.anyString(), Mockito.anyString());

        mockMvc.perform(post("/member/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("RefreshToken", refreshToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "logout",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestHeaders(
                                List.of(
                                        headerWithName("Authorization").description("Access Token"),
                                        headerWithName("RefreshToken").description("Refresh Token")
                                )
                        )
                ));
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void test_reissue() throws Exception {
        //given
        Map<String, String> reissueToken = new HashMap<>();
        reissueToken.put("accessToken", "sdakjhfewkl213124" );
        reissueToken.put("refreshToken", "sdfhkj23h42893421sdkhf1");
        given(authService.reissue(Mockito.anyString()))
                .willReturn(reissueToken);

        mockMvc.perform(get("/member/reissue")
                        .header("RefreshToken", refreshToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer " + reissueToken.get("accessToken")))
                .andExpect(header().string("RefreshToken", reissueToken.get("refreshToken")))
                .andDo(document(
                        "reissue-Token",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestHeaders(headerWithName("RefreshToken").description("Refresh Token")),
                        responseHeaders(
                                List.of(
                                        headerWithName("Authorization").description("Access Token"),
                                        headerWithName("RefreshToken").description("Refresh Token")
                                )
                        )
                ));
    }


}
