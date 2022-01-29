package com.importH.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.importH.core.entity.base.BaseTimeEntity;
import lombok.*;

import javax.annotation.PreDestroy;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue
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

}
