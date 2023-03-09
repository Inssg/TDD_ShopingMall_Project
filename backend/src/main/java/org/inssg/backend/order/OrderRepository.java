package org.inssg.backend.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

     List<Order> findByMemberIdOrderByIdDesc(Long memberId);

     Order findByIdAndMemberId(Long orderId, Long memberId);

}
