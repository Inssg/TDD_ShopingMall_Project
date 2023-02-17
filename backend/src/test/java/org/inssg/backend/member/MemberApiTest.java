package org.inssg.backend.member;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.inssg.backend.util.ApiDocumentUtils.getRequestPreProcessor;
import static org.inssg.backend.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class MemberApiTest {

    @Autowired
    private Gson gson;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    //Todo ApiTest로 변경
    @Test
    @DisplayName("회원가입")
    @WithMockUser
    void 회원가입() throws Exception {
        //given
        String email = "abc@gmail.com";
        String password = "test1234!";
        String username = "테스트1";
        MemberCreate memberCreate = new MemberCreate(email, password, username);

        given(memberService.createMember(Mockito.any(MemberCreate.class)))
                .willReturn(1L);

        String content = gson.toJson(memberCreate);
        //when
        ResultActions actions = mockMvc.perform(
                post("/members/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(csrf())
        );

        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(document(
                        "post-signUp",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestFields(
                                List.of(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호"),
                                        fieldWithPath("username").type(JsonFieldType.STRING).description("회원 닉네임")
                                )
                        ),
                        responseFields(
                                List.of(fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 회원 ID"))
                        )
                ));
    }

}
