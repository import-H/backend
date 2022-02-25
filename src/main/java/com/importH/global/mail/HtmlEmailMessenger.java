package com.importH.global.mail;


import com.importH.domain.user.dto.EmailMessageDto;
import com.importH.global.error.code.UserErrorCode;
import com.importH.global.error.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@Profile(value = "dev")
@RequiredArgsConstructor
public class HtmlEmailMessenger implements EmailMessenger {

    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendEmail(EmailMessageDto emailMessageDto) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessageDto.getTo());
            mimeMessageHelper.setSubject(emailMessageDto.getSubject());
            mimeMessageHelper.setText(emailMessageDto.getMessage(), true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send email", e);
            throw new UserException(UserErrorCode.NOT_VALID_EMAIL);
        }
    }

}
