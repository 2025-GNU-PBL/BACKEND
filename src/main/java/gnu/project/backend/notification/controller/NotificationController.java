package gnu.project.backend.notification.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.notification.controller.docs.NotificationDocs;
import gnu.project.backend.notification.dto.response.NotificationResponseDto;
import gnu.project.backend.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController implements NotificationDocs {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(final @Auth Accessor accessor) {
        return notificationService.subscribe(accessor);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications(
        @Auth final Accessor accessor) {
        return ResponseEntity.ok(
            notificationService.getUnreadNotifications(accessor)
        );
    }

    @GetMapping()
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications(
        @Auth final Accessor accessor) {
        return ResponseEntity.ok(
            notificationService.getAllNotifications(accessor)
        );
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@Auth final Accessor accessor) {
        Long count = notificationService.getUnreadCount(accessor);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDto> markAsRead(@Auth final Accessor accessor,
        @PathVariable final Long notificationId) {

        return ResponseEntity.ok(notificationService.markAsRead(accessor, notificationId));
    }

    /**
     * 모든 알림 읽음 처리
     */
    @PatchMapping()
    public ResponseEntity<List<NotificationResponseDto>> markAllAsRead(
        @Auth final Accessor accessor) {
        return ResponseEntity.ok(notificationService.markAllAsRead(accessor));
    }

    /**
     * 특정 알림 삭제
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@Auth final Accessor accessor,
        @PathVariable final Long notificationId) {
        notificationService.deleteNotification(accessor, notificationId);
        return ResponseEntity.noContent().build();
    }

}
