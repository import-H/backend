package com.importH.core.service;

import com.importH.domain.user.dto.EmailDto;
import com.importH.domain.user.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    EmailService consoleEmailService;

    @Test
    @DisplayName("[성공] 이메일 보내기")
    void sendEmail_success() throws Exception {
        // given
        EmailDto dto = EmailDto.builder()
                .email("test@email.com")
                .link("link")
                .message("test")
                .nickname("테스트")
                .linkName("이메일 인증하기")
                .subject("제목")
                .build();

        // when
        // then
        consoleEmailService.sendEmail(dto);
    }

    @Test
    @DisplayName("[실패] 이메일 보내기 - 필수 입력값 빠짐")
    void sendEmail_fail() throws Exception {
        // given
        EmailDto dto = EmailDto.builder()
                .email("test@email.com")
                .nickname("테스트")
                .linkName("이메일 인증하기")
                .build();

        // when
        // then
        assertThrows(ConstraintViolationException.class , () -> consoleEmailService.sendEmail(dto));
    }

}