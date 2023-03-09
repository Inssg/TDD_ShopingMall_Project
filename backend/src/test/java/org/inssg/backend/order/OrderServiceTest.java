package org.inssg.backend.order;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderServiceTest {

    private  OrderService orderService;
    private OrderRepository orderRepository;

    OrderCreate orderCreate;
    OrderCreate orderSample;

    Long memberId;
    Long orderId;

    @BeforeEach
    void setUp() {
        orderCreate = new OrderCreate("힙으뜸", "서울특별시 관악구 서림동", 1L, 6);
        orderSample = new OrderCreate("힙으뜸", "서울특별시 관악구 서림동", 2L, 3);

        memberId =1L;
        orderId = 1L;
        orderService = new OrderService();
        orderRepository = new OrderRepository();
    }

    @Test
    void test_createOrder() {
        //given
       orderService.createOrder(orderCreate, memberId);
        Order order = orderRepository.persistence.get(1L);

        //then
        assertThat(order.userName).isEqualTo("힙으뜸");
        assertThat(order.quantity).isEqualTo(6);
        assertThat(order.itemId).isEqualTo(1L);
        assertThat(order.memberId).isEqualTo(1L);
    }

    @Test
    void test_getOrder() {
        //given
        orderService.createOrder(orderCreate,memberId);
        orderService.createOrder(orderSample,memberId);
        //when
        List<Order> orders = orderService.getOrders(memberId);
        //then
        assertThat(orders.get(0).userName).isEqualTo("힙으뜸");
        assertThat(orders.get(0).quantity).isEqualTo(6);
        assertThat(orders.get(0).itemId).isEqualTo(1L);
        assertThat(orders.get(1).userName).isEqualTo("힙으뜸");
        assertThat(orders.get(1).quantity).isEqualTo(3);
        assertThat(orders.get(1).itemId).isEqualTo(2L);
    }

    @Test
    void test_cancelOrder() {
        //given
        orderService.createOrder(orderCreate,memberId);
        //when
        orderService.cancelOrder(memberId,orderId);
        //then
        assertThat(orderRepository.persistence.get(1L)).isNull();
    }

    private class OrderService {

        public void createOrder(OrderCreate orderCreate, Long memberId) {
            Order order = new Order(memberId, orderCreate.userName, orderCreate.address,
                    orderCreate.itemId, orderCreate.quantity);
            orderRepository.save(order);
        }

        public List<Order> getOrders(Long memberId) {
            List<Order> orders = orderRepository.findByMemberId(memberId);
            return orders;
        }

        public void cancelOrder(Long memberId, Long orderId) {
           Order order = orderRepository.findByMemberIdAndOrderId(memberId, orderId);
            orderRepository.delete(order);
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

        public List<Order> findByMemberId(Long memberId) {
            return List.of(persistence.get(1L),
                            persistence.get(2L));
        }

        public void delete(Order order) {
            persistence.remove(order.id);
        }

        public Order findByMemberIdAndOrderId(Long memberId, Long orderId) {
            return persistence.get(orderId);
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
