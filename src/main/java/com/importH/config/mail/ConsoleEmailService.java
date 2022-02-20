package com.importH.config.mail;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Primary
@Component
@Slf4j
@NoArgsConstructor
public class ConsoleEmailService implements EmailMessenger {

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email: {} ", emailMessage.getMessage());
    }
}
