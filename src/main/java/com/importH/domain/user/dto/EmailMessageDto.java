package com.importH.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailMessageDto {

    private String to;

    private String subject;

    private String message;
}
