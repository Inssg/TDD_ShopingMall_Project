package org.inssg.backend.security.refreshtoken;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @Column(name = "rt_key")
    private String key;

    @Column(name = "rt_value")
    private String value;

    @Builder
    public RefreshToken(String email, String value) {
        this.key = email;
        this.value = value;
    }

    public void updateValue(String token) {
        this.value = token;
    }
}
