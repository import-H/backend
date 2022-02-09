package com.importH.core.domain.user;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class InfoAgree {


    private boolean infoByEmail;

    private boolean infoByWeb;
}
