package org.inssg.backend.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void createOrder(OrderCreate orderCreate, Long memberId) {
        Order order = Order.create(memberId, orderCreate);
        orderRepository.save(order);
    }

    public List<Order> getOrders(Long memberId) {
        List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);
        return orders;
    }

    public void cancelOrder(Long memberId, Long orderId) {
        Order order = orderRepository.findByIdAndMemberId(orderId, memberId);
        orderRepository.delete(order);
    }
}
