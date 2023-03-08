package org.inssg.backend.cart;

import lombok.RequiredArgsConstructor;
import org.inssg.backend.annotation.AuthMember;
import org.inssg.backend.security.userdetails.MemberDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("items/{itemId}")
    public ResponseEntity create(@PathVariable("itemId") Long itemId,
                                 @RequestParam int quantity,
                                 @AuthMember MemberDetails memberDetails) {

        Long memberId = memberDetails.getMemberId();
        Cart cart = cartService.addCartItem(itemId, memberId, quantity);
        return new ResponseEntity(cart, HttpStatus.OK);
    }


}
