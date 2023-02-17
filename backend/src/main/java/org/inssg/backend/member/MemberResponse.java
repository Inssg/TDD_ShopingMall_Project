package org.inssg.backend.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {
    private Long id;

    @Builder
    public MemberResponse(Long id) {
        this.id = id;
    }

    public static MemberResponse toResponse(Long memberId) {
        return MemberResponse.builder()
                .id(memberId)
                .build();
    }
}
