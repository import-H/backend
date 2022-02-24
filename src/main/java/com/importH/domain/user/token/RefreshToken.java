package com.importH.domain.user.token;

import com.importH.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    @Lob
    @Column(nullable = false)
    private String token;

    public static RefreshToken create(Long userId, String refreshToken) {
        return RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .build();
    }

    public RefreshToken updateToken(String token) {
        this.token = token;
        return this;
    }

}
