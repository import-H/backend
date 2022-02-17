package com.importH.core.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("이메일 토큰 생성 후 1시간 후라 이메일 보내기 가능")
    void impossibleResend_success() throws Exception {

        User user = User.builder().nickname("테스트").emailCheckTokenGeneratedAt(LocalDateTime.now().minusHours(2)).build();

        assertThat(user.canSendConfirmEmail()).isTrue();
    }

    @Test
    @DisplayName("이메일 토큰 생성 후 1시간이 지나지 않아 이메일 보낼 수 없음")
    void impossibleResend_fail() throws Exception {

        User user = User.builder().nickname("테스트").build();
        user.generateEmailToken();

        assertThat(user.canSendConfirmEmail()).isFalse();
    }

}