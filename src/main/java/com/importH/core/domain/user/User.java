package com.importH.core.domain.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.importH.core.domain.base.BaseTimeEntity;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.user.UserDto;
import com.importH.core.dto.user.UserDto.Request;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(length = 20,nullable = false)
    private String nickname;

    private String role;

    @Lob
    private String profileImage;

    @Column(length = 100)
    private String introduction;

    @Column(nullable = false)
    private Boolean weekAgree;

    private String personalUrl;

    @Embedded
    @Builder.Default
    private InfoAgree infoAgree = new InfoAgree();

    private boolean deleted;

    @ManyToMany
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
}
