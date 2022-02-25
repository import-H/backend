package com.importH.domain.user.service;


import com.importH.domain.user.dto.EmailDto;
import com.importH.domain.user.dto.EmailMessageDto;
import com.importH.global.config.AppProperties;
import com.importH.global.mail.EmailMessenger;
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
        EmailMessageDto emailMessageDto = getEmailMessage(message, emailDto.getEmail(), emailDto.getSubject());

        emailMessenger.sendEmail(emailMessageDto);
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

    private EmailMessageDto getEmailMessage(String message, String email, String subject) {
        return EmailMessageDto.builder()
                .to(email)
                .subject(subject)
                .message(message)
                .build();
    }

    public String createMessage(Context context) {
        return templateEngine.process("mail/simple-link", context);
    }
}
