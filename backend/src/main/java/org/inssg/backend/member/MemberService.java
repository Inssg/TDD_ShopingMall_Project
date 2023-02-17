package org.inssg.backend.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //Todo: PasswordEncoder 로 encoding하는 로직으로 변경 필요
    //Todo: findByEmail Custom Exception으로 변경 필요
    public Long createMember(MemberCreate memberCreate) {

        Member member = Member.create(memberCreate, passwordEncoder);
        memberRepository.save(member);

        return findMember(member.getEmail()).getId();
    }


    private Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException());
    }
}
