package org.inssg.backend.security.userdetails;

import lombok.RequiredArgsConstructor;
import org.inssg.backend.error.BusinessLogicException;
import org.inssg.backend.error.ErrorResponse;
import org.inssg.backend.error.ExceptionCode;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return MemberDetails.of(member);
    }
}
