package org.inssg.backend.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberCreate;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderApiTest {

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


    }

    @Test
    void 주문_요청() throws Exception {
        //given
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
                .andExpect(jsonPath("$.quantity").value(4));
    }

    @Test
    void 주문_조회() throws Exception {
        //given
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
                .andExpect(jsonPath("$.[1].quantity").value(4));

    }

    @Test
    void 주문_취소() {
        Long orderId = 1L;
        mockMvc.perform(delete("/orders/{orderId}", ))

    }


}
