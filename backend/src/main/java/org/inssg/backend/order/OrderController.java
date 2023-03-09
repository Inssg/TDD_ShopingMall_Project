package org.inssg.backend.order;

import lombok.RequiredArgsConstructor;
import org.inssg.backend.annotation.AuthMember;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity createOrder(@RequestBody @Valid OrderCreate orderCreate,
                                      @AuthMember MemberDetails memberDetails) {
        Long memberId = memberDetails.getMemberId();
        Order order = orderService.createOrder(orderCreate, memberId);

        return new ResponseEntity(order,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getOrders(@AuthMember MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        List<Order> orders = orderService.getOrders(memberId);

        return new ResponseEntity(orders, HttpStatus.OK);
    }


}
