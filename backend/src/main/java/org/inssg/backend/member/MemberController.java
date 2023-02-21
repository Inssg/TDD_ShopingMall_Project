package org.inssg.backend.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    //Todo: authorityUtils 추가 필요
    //회원가입
    @PostMapping("/signup")
    public ResponseEntity create(@RequestBody @Valid MemberCreate memberCreate) {

        Long memberId = memberService.createMember(memberCreate);
        MemberResponse memberResponse = MemberResponse.toResponse(memberId);

        return new ResponseEntity<>(memberResponse, HttpStatus.CREATED);
    }
}
