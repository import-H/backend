package com.importH.global.mail;

import com.importH.domain.user.CustomUser;
import com.importH.domain.user.entity.User;
import com.importH.global.error.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.importH.global.error.code.UserErrorCode.NOT_VERIFIED_EMAIL;

@Slf4j
@Component
public class EmailVerifyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!request.getMethod().equals("GET") && authentication != null && authentication.getPrincipal() instanceof CustomUser) {
            User user = ((CustomUser) authentication.getPrincipal()).getUser();

            if (!user.isEmailVerified()) {
                throw new UserException(NOT_VERIFIED_EMAIL);
            }
        }

        return true;
    }


}
