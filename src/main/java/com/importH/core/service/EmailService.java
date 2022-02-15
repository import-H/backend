package com.importH.core.service;


import com.importH.config.AppProperties;
import com.importH.config.mail.EmailMessage;
import com.importH.config.mail.EmailMessenger;
import com.importH.core.dto.email.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;

@Service
@Validated
@RequiredArgsConstructor
public class EmailService {


    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailMessenger emailMessenger;

    public void sendEmail(@Valid EmailDto emailDto) {

        Context context = createContext(emailDto);
        String message = createMessage(context);
        EmailMessage emailMessage = getEmailMessage(message, emailDto.getEmail(), emailDto.getSubject());

        emailMessenger.sendEmail(emailMessage);
    }

    public Context createContext(EmailDto emailDto) {
        Context context = new Context();
        context.setVariable("link", emailDto.getLink());
        context.setVariable("nickname", emailDto.getNickname());
        context.setVariable("linkName", emailDto.getLinkName());
        context.setVariable("message", emailDto.getMessage());
        context.setVariable("host", appProperties.getHost());
        return context;
    }

    private EmailMessage getEmailMessage(String message, String email, String subject) {
        return EmailMessage.builder()
                .to(email)
                .subject(subject)
                .message(message)
                .build();
    }

    public String createMessage(Context context) {
        return templateEngine.process("mail/simple-link", context);
    }
}
