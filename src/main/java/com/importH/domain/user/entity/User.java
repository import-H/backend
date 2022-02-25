package com.importH.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.importH.domain.BaseTimeEntity;
import com.importH.domain.tag.Tag;
import com.importH.domain.user.dto.UserDto.Request;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(length = 20, nullable = false)
    private String nickname;

    private String role;

    private String profileImage;

    @Column(length = 100)
    private String introduction;

    @Builder.Default
    private Boolean weekAgree = false;

    @Column(unique = true, length = 20)
    private String pathId;

    private String personalUrl;

    private String oauthId;

    @Embedded
    @Builder.Default
    private InfoAgree infoAgree = new InfoAgree();

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private boolean deleted;

    private LocalDateTime deletedTime;

    @ManyToMany
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public void setPassword(String encodePassword) {
        this.password = encodePassword;
    }

    public void update(Request request) {
        InfoAgree infoAgree = InfoAgree.builder()
                .infoByWeb(request.isInfoByWeb())
                .infoByEmail(request.isInfoByEmail())
                .build();

        this.nickname = request.getNickname();
        this.infoAgree = infoAgree;
        this.profileImage = request.getProfileImage();
        this.introduction = request.getIntroduction();
        this.personalUrl = request.getPersonalUrl();
    }

    public void delete() {
        deleted = true;
        nickname = "삭제된 계정";
        email = "deleted" + getId();
        password = "deleted" + getId();
        profileImage = "N";
        role = null;
        deletedTime = LocalDateTime.now();
    }

    public void generateEmailToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.getEmailCheckToken() != null && this.getEmailCheckToken().equals(token);
    }

    public void completeSignup() {
        this.emailVerified = true;
    }

    public boolean canSendConfirmEmail() {
        return emailCheckTokenGeneratedAt == null || emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    public User update(String email, String imageUrl) {
        this.email = email;
        this.profileImage = imageUrl;
        return this;
    }
}
