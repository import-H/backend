package com.importH.domain.notification;

import com.importH.domain.user.CurrentUser;
import com.importH.domain.user.entity.User;
import com.importH.global.response.ListResult;
import com.importH.global.response.ResponseService;
import com.importH.global.response.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return responseService.getListResult(notificationService.findAll(user));
    }

    @ApiOperation(value = "알림 읽기", notes = "알람 내용을 확인합니다.")
    @GetMapping("/{messageId}")
    public SingleResult<String> checkNotification(
            @ApiIgnore @CurrentUser User user,
            @ApiParam(value = "알림 id", example = "1") @PathVariable Long messageId) {
        return responseService.getSingleResult(notificationService.checkNotification(user,messageId));
    }
}
