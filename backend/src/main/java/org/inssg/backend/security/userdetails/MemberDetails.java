package org.inssg.backend.security.userdetails;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.inssg.backend.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemberDetails extends Member implements UserDetails {

    private Long memberId;
    private String email;
    private String password;
    private List<String> roles;

    @Builder
    private MemberDetails(String email, String password,
                         Long memberId, List<String> roles) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public static MemberDetails of(Member member) {
        return MemberDetails.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .roles(List.of(member.getAuthority().toString()))
                .build();
    }
    //Todo: authorityutil 추가시 변경필요
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(roles.get(0)));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
