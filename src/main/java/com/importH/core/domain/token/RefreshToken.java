package com.importH.core.domain.token;

import com.importH.core.domain.base.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(nullable = false)
    private Long key;

    @Column(nullable = false)
    private String token;

    public static RefreshToken create(Long userId, String refreshToken) {
        return RefreshToken.builder()
                .key(userId)
                .token(refreshToken)
                .build();
    }

    public RefreshToken updateToken(String token) {
        this.token = token;
        return this;
    }

    @Builder
    public RefreshToken(Long key, String token) {
        this.key = key;
        this.token = token;
    }

}
