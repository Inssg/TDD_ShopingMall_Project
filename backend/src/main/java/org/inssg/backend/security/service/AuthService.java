package org.inssg.backend.security.service;

import lombok.RequiredArgsConstructor;
import org.inssg.backend.member.Member;
import org.inssg.backend.member.MemberNotFound;
import org.inssg.backend.member.MemberRepository;
import org.inssg.backend.security.TokenNotValid;
import org.inssg.backend.security.jwt.JwtTokenProvider;
import org.inssg.backend.security.redis.RedisService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final RedisService redisService;


    public Map<String, Object> reissue(String refreshTokenValue) {
        HashMap<String, Object> reissuedToken = new HashMap<>();
        String email = jwtTokenProvider.getEmailFromRefreshToken(refreshTokenValue);

        String refreshToken = redisService.getValues(email);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFound());

        if (!refreshToken.equals(refreshTokenValue) || refreshToken.isEmpty() ) {
            throw new TokenNotValid();
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(member);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member);
        reissuedToken.put("accessToken", newAccessToken);
        reissuedToken.put("refreshToken", newRefreshToken);

        //refreshToken Redis 업데이트
        redisService.setValues(email,newRefreshToken,jwtTokenProvider.getRefreshTokenExpirationMinutes());

        return reissuedToken;
    }

    public void logout(String accessTokenValue, String refreshTokenValue) {
        String email = jwtTokenProvider.getEmailFromRefreshToken(refreshTokenValue);
        Long untilExpiration = jwtTokenProvider.calExpDuration(accessTokenValue);

        redisService.deleteValues(email);
        redisService.setBlackListValues(accessTokenValue, "BlackList", untilExpiration);

    }
}

