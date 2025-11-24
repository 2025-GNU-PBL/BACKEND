package gnu.project.backend.notification.entity;

import static gnu.project.backend.common.enumerated.UserRole.CUSTOMER;
import static gnu.project.backend.common.enumerated.UserRole.OWNER;
import static gnu.project.backend.notification.enumerated.NotificationType.PAYMENT_CANCELED;
import static gnu.project.backend.notification.enumerated.NotificationType.PAYMENT_CANCEL_REQUEST;
import static gnu.project.backend.notification.enumerated.NotificationType.PAYMENT_COMPLETED;
import static gnu.project.backend.notification.enumerated.NotificationType.PAYMENT_REQUIRED;
import static gnu.project.backend.notification.enumerated.NotificationType.RESERVATION_COMPLETED;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.notification.enumerated.NotificationType;
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
    private Long recipientId;

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

    @Column(nullable = false)
    private Boolean isRead = false;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private Boolean isSent = false;

    private LocalDateTime sentAt;

    private LocalDateTime expiresAt;

    public static Notification createPaymentNotification(
        final Long recipientId,
        final Long reservationId,
        final String reservationTitle,
        final String paymentUrl
    ) {
        Notification notification = new Notification();
        notification.recipientId = recipientId;
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

    public static Notification createReservationNotification(
        final Long recipientId,
        final Long reservationId,
        final String reservationTitle,
        final String paymentUrl
    ) {
        Notification notification = new Notification();
        notification.recipientId = recipientId;
        notification.recipientRole = OWNER;
        notification.type = RESERVATION_COMPLETED;
        notification.title = "예약 요청";
        notification.message = String.format("'%s' 예약이 들어왔습니다. 확인을 부탁드립니다..",
            reservationTitle);
        notification.reservationId = reservationId;
        notification.actionUrl = paymentUrl;
        notification.expiresAt = LocalDateTime.now().plusDays(3);
        return notification;
    }

    public static Notification createPaymentCompleted(
        Long recipientId,
        Long reservationId,
        String reservationTitle,
        UserRole userRole
    ) {
        Notification notification = new Notification();
        notification.recipientId = recipientId;
        notification.recipientRole = userRole;
        notification.type = PAYMENT_COMPLETED;
        notification.title = "결제 완료";
        notification.message = String.format("'%s' 예약 결제가 완료되었습니다.", reservationTitle);
        notification.reservationId = reservationId;
        notification.actionUrl = "/reservations/" + reservationId;
        notification.isSent = false;
        return notification;
    }

    public static Notification createPaymentCancelRequested(
        Long recipientId,
        Long reservationId,
        String reservationTitle
    ) {
        Notification notification = new Notification();
        notification.recipientId = recipientId;
        notification.recipientRole = OWNER;
        notification.type = PAYMENT_CANCEL_REQUEST;
        notification.title = "결제 취소 요청";
        notification.message = String.format("'%s' 예약에 대해 결제 취소 요청이 접수되었습니다.", reservationTitle);
        notification.reservationId = reservationId;
        notification.actionUrl = "/reservations/" + reservationId;
        return notification;
    }

    public static Notification createPaymentCancelApproved(
        Long recipientId,
        Long reservationId,
        String reservationTitle
    ) {
        Notification notification = new Notification();
        notification.recipientId = recipientId;
        notification.recipientRole = CUSTOMER;
        notification.type = PAYMENT_CANCELED;
        notification.title = "결제 취소 승인";
        notification.message = String.format("'%s' 예약 결제 취소가 승인되었습니다.", reservationTitle);
        notification.reservationId = reservationId;
        notification.actionUrl = "/reservations/" + reservationId;
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