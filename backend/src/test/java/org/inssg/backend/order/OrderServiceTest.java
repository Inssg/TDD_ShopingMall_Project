package org.inssg.backend.order;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderServiceTest {

    private  OrderService orderService;
    private OrderRepository orderRepository;
    OrderCreate orderCreate;
    String userName;
    String address;
    Long itemId;
    int quantity;
    Long memberId;

    @BeforeEach
    void setUp() {
        userName = "힙으뜸";
        address = "서울특별시 관악구 서리몽";
        itemId = 1L;
        quantity = 6;
        memberId=1L;
        orderCreate = new OrderCreate(userName, address, itemId, quantity);

        orderService = new OrderService();
        orderRepository = new OrderRepository();
    }

    @Test
    void test_createOrder() {
        //given
       orderService.createOrder(orderCreate, memberId);
        Order order = orderRepository.persistence.get(1L);

        //then
        assertThat(order.userName).isEqualTo(userName);
        assertThat(order.quantity).isEqualTo(6);
        assertThat(order.itemId).isEqualTo(1L);
        assertThat(order.memberId).isEqualTo(1L);
    }

    private class OrderService {
        public void createOrder(OrderCreate orderCreate, Long memberId) {
            Order order = new Order(memberId, orderCreate.userName, orderCreate.address,
                    orderCreate.itemId, orderCreate.quantity);
            orderRepository.save(order);

        }
    }

    private class Order {
        private Long id;

        private Long memberId;

        @Column(length = 50, nullable = false)
        private String userName;

        @Column(length = 500, nullable = false)
        private String address;

        private Long itemId;

        private int quantity;

        public Order(Long memberId, String userName, String address, Long itemId, int quantity) {
            this.memberId = memberId;
            this.userName = userName;
            this.address = address;
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    private class OrderRepository {
        Map<Long, Order> persistence = new HashMap<>();
        Long sequence = 0L;

        public void save(Order order) {
            order.setId(++sequence);
            persistence.put(order.id, order);
        }
    }


    private class OrderCreate {
        private String userName;
        private String address;
        private Long itemId;
        private int quantity;

        public OrderCreate(String userName, String address, Long itemId, int quantity) {
            this.userName = userName;
            this.address = address;
            this.itemId = itemId;
            this.quantity = quantity;
        }
    }
}
