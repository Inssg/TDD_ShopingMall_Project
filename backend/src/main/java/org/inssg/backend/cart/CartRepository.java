package org.inssg.backend.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

     List<Cart> findByMemberId(Long memberId);

     Cart findByMemberIdAndItemId(Long memberId, Long itemId);

}
