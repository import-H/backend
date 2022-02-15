package com.importH.config.mail;

import com.importH.core.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Slf4j
public class ConsoleEmailService implements EmailMessenger {

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("sent email: {} ", emailMessage.getMessage());
    }
}
