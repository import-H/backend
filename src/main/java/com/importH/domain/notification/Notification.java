package com.importH.domain.notification;

import com.importH.domain.BaseTimeEntity;
import com.importH.domain.user.entity.User;
import com.importH.global.event.PostUpdatedEventDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private String title;

    private String link;

    @Builder.Default
    private boolean checked = false;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Notification create(PostUpdatedEventDto dto) {

        Notification notification = Notification.builder()
                .notificationType(dto.getNotificationType())
                .title(dto.getMsg())
                .user(dto.getPost().getUser())
                .link(dto.getUri())
                .build();

        return notification;
    }


    public void checked() {
        this.checked = true;
    }


}
