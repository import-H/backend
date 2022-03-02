package com.importH.domain.notification;

import com.importH.domain.user.CurrentUser;
import com.importH.domain.user.entity.User;
import com.importH.global.response.ListResult;
import com.importH.global.response.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "8. Notification")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/messages")
public class NotificationController {

    private final ResponseService responseService;
    private final NotificationService notificationService;

    @ApiOperation(value = "알림 가져오기", notes = "로그인한 유저의 알람 목록을 가져옵니다.")
    @GetMapping
    public ListResult<NotificationDto.Response> findAllNotification(
            @ApiIgnore @CurrentUser User user
            ) {
        return responseService.getListResult(notificationService.findAll(user.getId()));
    }

}
