package org.inssg.backend.security;

import lombok.RequiredArgsConstructor;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberNotFound;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.refreshtoken.RefreshToken;
import org.inssg.backend.security.refreshtoken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;


    public Map<String, Object> reissue(String accessTokenValue, String refreshTokenValue) {
        HashMap<String, Object> reissuedToken = new HashMap<>();
        String email = jwtTokenProvider.getEmailFromRefreshToken(refreshTokenValue);

        RefreshToken refreshToken = refreshTokenRepository.findByKey(email).orElseThrow(() -> new TokenNotExist());
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFound());

        if (!refreshToken.getValue().equals(refreshTokenValue)) {
            throw new TokenNotAccord();
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(member);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
        reissuedToken.put("accessToken", newAccessToken);
        reissuedToken.put("refreshToken", newRefreshToken);

        refreshToken.updateValue(newRefreshToken);
        refreshTokenRepository.save(refreshToken);

        return reissuedToken;
    }
}
