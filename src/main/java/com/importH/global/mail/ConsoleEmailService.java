package com.importH.global.mail;

import com.importH.domain.user.dto.EmailMessageDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile(value = "local")
@NoArgsConstructor
public class ConsoleEmailService implements EmailMessenger {

    @Override
    public void sendEmail(EmailMessageDto emailMessageDto) {
        log.info("sent email: {} ", emailMessageDto.getMessage());
    }
}
