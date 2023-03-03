package org.inssg.backend.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private Authority authority = Authority.ROLE_USER;

    //부모의 Builder와 자식의 Builder 간의 이름이 동일하여 충돌이 발생할 수 있으므로 이에 대해 각각 이름을 지정해주려 할 때 사용
    @Builder(builderMethodName = "createBuilder")
    public Member(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public static Member create(MemberCreate memberCreate, PasswordEncoder passwordEncoder) {
       return  Member.createBuilder()
                .email(memberCreate.getEmail())
                .password(passwordEncoder.encode(memberCreate.getPassword()))
                .username(memberCreate.getUsername())
                .build();
    }

}
