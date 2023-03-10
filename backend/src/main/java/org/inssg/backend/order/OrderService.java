package org.inssg.backend.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderCreate orderCreate, Long memberId) {
        Order order = Order.create(memberId, orderCreate);
        return orderRepository.save(order);
    }

    @Transactional(readOnly= true)
    public List<Order> getOrders(Long memberId) {
        List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);
        return orders;
    }

    public void cancelOrder(Long memberId, Long orderId) {
        Order order = orderRepository.findByIdAndMemberId(orderId, memberId);
        orderRepository.delete(order);
    }
}
