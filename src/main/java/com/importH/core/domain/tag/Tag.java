package com.importH.core.domain.tag;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Tag {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

}

