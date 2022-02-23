package com.importH.global.mail;


import com.importH.domain.user.dto.EmailMessageDto;

public interface EmailMessenger {

    void sendEmail(EmailMessageDto emailMessageDto);
}
