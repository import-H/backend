package com.importH.core.domain.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.importH.core.domain.base.BaseTimeEntity;
import com.importH.core.domain.tag.Tag;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Account extends BaseTimeEntity {

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
    private String nickName;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Lob
    private String profileImage;

    @Column(length = 100)
    private String introduction;

    @Column(nullable = false)
    private Boolean weekAgree;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

}
