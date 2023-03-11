package org.inssg.backend.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.inssg.backend.util.AcceptanceTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.inssg.backend.util.ApiDocumentUtils.getRequestPreProcessor;
import static org.inssg.backend.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderApiTest extends AcceptanceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderRepository orderRepository;

    @MockBean
    OrderService orderService;

    MemberDetails memberDetails;

    OrderCreate orderCreate;
    OrderCreate orderSample;
    Order order;
    Order order2;

    @BeforeAll
    public void setup() {
        String email = "abc@gmail.com";
        String password = "1234";
        String username = "테스트";

        MemberCreate memberCreate = MemberCreate.builder().username(username).password(password).email(email).build();
        Member member = Member.create(memberCreate, passwordEncoder);
        memberRepository.save(member);
        memberDetails = MemberDetails.of(member);

        orderCreate = OrderCreate.builder().userName("테스트").address("경기도 성남시 판교로").itemId(1L).quantity(4).build();
        orderSample = OrderCreate.builder().userName("테스트").address("경기도 성남시 판교로").itemId(2L).quantity(2).build();

        order = Order.create(1L, orderCreate);
        order2 = Order.create(1L, orderSample);

        orderRepository.save(order);
        orderRepository.save(order2);

    }

    @Test
    void 주문_요청() throws Exception {
        //given
        orderRepository.save(order);
        given(orderService.createOrder(Mockito.any(), Mockito.anyLong()))
                .willReturn(order);

        mockMvc.perform((post("/orders"))
                        .with(user(memberDetails))
                        .content(mapper.writeValueAsString(orderCreate))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.userName").value("테스트"))
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.quantity").value(4))
                .andDo(document(
                        "create-order",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestFields(
                                List.of(
                                        fieldWithPath("userName").type(JsonFieldType.STRING).description("회원 이름"),
                                        fieldWithPath("address").type(JsonFieldType.STRING).description("회원 주소"),
                                        fieldWithPath("itemId").type(JsonFieldType.NUMBER).description("주문할 제품 식별자"),
                                        fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("제품 주문 수량")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("주문 식별자"),
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("userName").type(JsonFieldType.STRING).description("회원 이름"),
                                        fieldWithPath("address").type(JsonFieldType.STRING).description("회원 주소"),
                                        fieldWithPath("itemId").type(JsonFieldType.NUMBER).description("주문한 제품 식별자"),
                                        fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("주문한 제품 수량")
                                )
                        )
                ));
    }

    @Test
    void 주문_조회() throws Exception {
        //given
        orderRepository.save(order);
        orderRepository.save(order2);
        given(orderService.getOrders(Mockito.anyLong()))
                .willReturn(List.of(order2, order));

        mockMvc.perform((get("/orders"))
                        .with(user(memberDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].userName").value("테스트"))
                .andExpect(jsonPath("$.[0].itemId").value(2L))
                .andExpect(jsonPath("$.[0].quantity").value(2))
                .andExpect(jsonPath("$.[1].itemId").value(1L))
                .andExpect(jsonPath("$.[1].quantity").value(4))
                .andDo(document(
                        "get-orders",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        responseFields(
                                List.of(
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("주문 식별자"),
                                        fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("[].userName").type(JsonFieldType.STRING).description("회원 이름"),
                                        fieldWithPath("[].address").type(JsonFieldType.STRING).description("회원 주소"),
                                        fieldWithPath("[].itemId").type(JsonFieldType.NUMBER).description("주문한 제품 식별자"),
                                        fieldWithPath("[].quantity").type(JsonFieldType.NUMBER).description("주문한 제품 수량")
                                )
                        )
                ));

    }

    @Test
    void 주문_취소() throws Exception {
        //given
        Long orderId = 1L;
        willDoNothing().given(orderService).cancelOrder(Mockito.anyLong(),Mockito.anyLong());

        mockMvc.perform(delete("/orders/{orderId}", orderId)
                        .with(user(memberDetails))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(
                        "remove-order",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(parameterWithName("orderId").description("취소할 주문 식별자"))
                ));

        verify(orderService,atLeastOnce()).cancelOrder(Mockito.anyLong(),Mockito.anyLong());

    }


}
