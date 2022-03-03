package com.importH.global.event;

import com.importH.domain.notification.NotificationType;
import com.importH.domain.post.Post;
import lombok.Getter;

@Getter
public class PostUpdatedEventDto{
    private Post post;
    private final NotificationType notificationType;
    private String msg;


    public PostUpdatedEventDto(Post post, String msg) {
        this.post = post;
        this.notificationType = NotificationType.POST_UPDATED;
        this.msg = msg;
    }

    public String getUri() {
        return post.getType() + "/" + post.getId();
    }
}
