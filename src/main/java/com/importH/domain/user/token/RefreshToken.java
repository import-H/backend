package com.importH.domain.user.token;

import com.importH.domain.BaseTimeEntity;
import com.importH.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "refresh_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "refreshToken")
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(nullable = false)
    private String token;

    public static RefreshToken create(User user, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .build();
        user.setToken(token);
        return token;
    }

    public RefreshToken updateToken(String token) {
        this.token = token;
        return this;
    }

}
