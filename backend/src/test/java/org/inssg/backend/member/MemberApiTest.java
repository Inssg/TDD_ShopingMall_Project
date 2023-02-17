package org.inssg.backend.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberApiTest {

    private MemberService memberService;
    private PasswordEncoder passwordEncoder;
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberService = new MemberService();
        memberRepository = new MemberRepository();
        passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return false;
            }
        };
    }

    @Test
    @DisplayName("회원가입 테스트")
    void 회원가입() {
        //given
        String email = "abc@gmail.com";
        String password = "test1234!";
        String username = "테스트1";

        MemberCreate memberCreate = new MemberCreate(email, password, username);
        //when
        memberService.createMember(memberCreate);
        //then
        Member member = memberRepository.persistence.get(1L);

        assertThat(member.email).isEqualTo(email);
        assertThat(member.username).isEqualTo(username);
        assertThat(member.password).isEqualTo(password);

    }

    private class MemberCreate {
        private String email;
        private String password;
        private String username;

        public MemberCreate(String email, String password, String username) {
            this.email = email;
            this.password = password;
            this.username = username;
        }
    }

    private class MemberService {
        public void createMember(MemberCreate memberCreate) {
           Member member =  Member.create(memberCreate,passwordEncoder);
           memberRepository.save(member);
        }
    }

    public static class Member {
        private Long id;
        private String email;
        private String password;
        private String username;

        public Member(String email, String password, String username) {
            this.email = email;
            this.password = password;
            this.username = username;
        }

        public static Member create(MemberCreate memberCreate, PasswordEncoder passwordEncoder) {
            return new Member(memberCreate.email, passwordEncoder.encode(memberCreate.password), memberCreate.username);
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    private class MemberRepository {
        private Map<Long, Member> persistence = new HashMap<>();
        private Long sequence = 0L;

        public void save(Member member) {
            member.setId(++sequence);
            persistence.put(member.getId(), member);
        }
    }
}
