package gnu.project.backend.notification.dto.response;

import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.notification.entity.Notification;
import gnu.project.backend.notification.enumerated.NotificationType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResponseDto(
    Long id,
    Long recipientId,
    UserRole recipientRole,
    NotificationType type,
    String title,
    String message,
    Long reservationId,
    String actionUrl,
    Boolean isRead,
    LocalDateTime readAt,
    Boolean isSent,
    LocalDateTime sentAt,
    LocalDateTime createdAt,
    LocalDateTime expiresAt
) {

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
            .id(notification.getId())
            .recipientId(notification.getRecipientId())
            .recipientRole(notification.getRecipientRole())
            .type(notification.getType())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .reservationId(notification.getReservationId())
            .actionUrl(notification.getActionUrl())
            .isRead(notification.getIsRead())
            .readAt(notification.getReadAt())
            .isSent(notification.getIsSent())
            .sentAt(notification.getSentAt())
            .createdAt(notification.getCreatedAt())
            .expiresAt(notification.getExpiresAt())
            .build();
    }
}
