package gnu.project.backend.notificaiton.entity;

import static gnu.project.backend.common.enumerated.UserRole.CUSTOMER;
import static gnu.project.backend.notificaiton.enumerated.NotificationType.PAYMENT_REQUIRED;
import static gnu.project.backend.notificaiton.enumerated.NotificationType.RESERVATION_APPROVED;
import static gnu.project.backend.notificaiton.enumerated.NotificationType.RESERVATION_CANCELLED;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.notificaiton.enumerated.NotificationType;
import gnu.project.backend.reservation.enumerated.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole recipientRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Column()
    private Long reservationId;

    @Column(length = 500)
    private String actionUrl;

    // === 상태 관리 ===
    @Column(nullable = false)
    private Boolean isRead = false;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private Boolean isSent = false;

    private LocalDateTime sentAt;

    private LocalDateTime expiresAt;

    // === 생성 메서드 ===
    public static Notification createPaymentNotification(
        Long customerId,
        Long reservationId,
        String reservationTitle,
        String paymentUrl
    ) {
        Notification notification = new Notification();
        notification.customerId = customerId;
        notification.recipientRole = CUSTOMER;
        notification.type = PAYMENT_REQUIRED;
        notification.title = "예약 승인 완료";
        notification.message = String.format("'%s' 예약이 승인되었습니다. 결제를 진행해주세요.",
            reservationTitle);
        notification.reservationId = reservationId;
        notification.actionUrl = paymentUrl;
        notification.expiresAt = LocalDateTime.now().plusDays(3);
        return notification;
    }

    public static Notification createReservationStatusNotification(
        Long customerId,
        UserRole recipientRole,
        Long reservationId,
        String reservationTitle,
        Status status
    ) {
        Notification notification = new Notification();
        notification.customerId = customerId;
        notification.recipientRole = recipientRole;
        notification.reservationId = reservationId;

        switch (status) {
            case APPROVE -> {
                notification.type = RESERVATION_APPROVED;
                notification.title = "예약 승인";
                notification.message = String.format("'%s' 예약이 승인되었습니다.", reservationTitle);
                notification.actionUrl = "/reservations/" + reservationId;
            }
            case DENY -> {
                notification.type = RESERVATION_CANCELLED;
                notification.title = "예약 취소";
                notification.message = String.format("'%s' 예약이 취소되었습니다.", reservationTitle);
                notification.actionUrl = "/reservations/" + reservationId;
            }
        }

        return notification;
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}