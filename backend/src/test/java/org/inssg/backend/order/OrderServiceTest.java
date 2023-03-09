package org.inssg.backend.order;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceTest {

    @Autowired
    private  OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    OrderCreate orderCreate;
    OrderCreate orderSample;
    OrderCreate orderSample2;

    Long memberId;
    Long orderId;

    @BeforeAll
    void setUp() {
        memberId =1L;
        orderId = 1L;
        orderCreate = new OrderCreate("힙으뜸", "서울특별시 관악구 서림동", 1L, 6);
        orderSample = new OrderCreate("힙으뜸", "서울특별시 관악구 서림동", 2L, 3);
        orderSample2 = new OrderCreate("힙으뜸", "서울특별시 관악구 서림동", 3L, 5);

        orderService.createOrder(orderCreate, memberId);
        orderService.createOrder(orderSample,memberId);

    }

    @Test
    void test_createOrder() {
        //given
       Order order = orderRepository.findByIdAndMemberId(orderId, memberId);

        //then
        assertThat(order.getUserName()).isEqualTo("힙으뜸");
        assertThat(order.getQuantity()).isEqualTo(6);
        assertThat(order.getItemId()).isEqualTo(1L);
        assertThat(order.getMemberId()).isEqualTo(1L);
    }

    @Test
    void test_getOrder() {
        //given

        //when
        List<Order> orders = orderService.getOrders(memberId);
        //then
        assertThat(orders.get(0).getUserName()).isEqualTo("힙으뜸");
        assertThat(orders.get(0).getQuantity()).isEqualTo(3);
        assertThat(orders.get(0).getItemId()).isEqualTo(2L);
        assertThat(orders.get(1).getUserName()).isEqualTo("힙으뜸");
        assertThat(orders.get(1).getQuantity()).isEqualTo(6);
        assertThat(orders.get(1).getItemId()).isEqualTo(1L);
    }

    @Test
    void test_cancelOrder() {
        //given
        orderService.createOrder(orderSample2,memberId);
        assertThat(orderRepository.findByIdAndMemberId(3L, memberId)).isNotNull();
        //when
        orderService.cancelOrder(memberId,3L);
        //then
        assertThat(orderRepository.findByIdAndMemberId(3L, memberId)).isNull();
    }


}
