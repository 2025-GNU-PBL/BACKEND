package gnu.project.backend.notification.event;

import gnu.project.backend.notification.entity.Notification;
import gnu.project.backend.notification.event.dto.PaymentApprovedEvent;
import gnu.project.backend.notification.event.dto.PaymentCancelApprovedEvent;
import gnu.project.backend.notification.event.dto.PaymentCancelRequestedEvent;
import gnu.project.backend.notification.event.dto.ReservationRequestEvent;
import gnu.project.backend.notification.service.NotificationService;
import gnu.project.backend.reservation.event.ReservationApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationApproved(ReservationApprovedEvent event) {
        final Notification notification = Notification.createPaymentNotification(
            event.customerId(),
            event.reservationId(),
            event.title(),
            String.format("/payment?reservationId=%d", event.reservationId())
        );
        notificationService.createAndSendNotification(notification);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationRequest(final ReservationRequestEvent event) {
        final Notification notification = Notification.createReservationNotification(
            event.ownerId(),
            event.reservationId(),
            event.title(),
            String.format("/reservationId=%d", event.reservationId())
        );
        notificationService.createAndSendNotification(notification);
    }

    @Async
    @EventListener
    public void handlePaymentCancelApprove(final PaymentCancelApprovedEvent event) {
        final Notification notification = Notification.createPaymentCancelApproved(
            event.customerId(),
            event.reservationId(),
            event.title()
        );
        notificationService.createAndSendNotification(notification);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCancelRequest(final PaymentCancelRequestedEvent event) {
        final Notification notification = Notification.createPaymentCancelRequested(
            event.ownerId(),
            event.reservationId(),
            event.title()
        );
        notificationService.createAndSendNotification(notification);
    }


    @Async
    @EventListener
    public void handleProceedPayment(final PaymentApprovedEvent event) {
        final Notification notification = Notification.createPaymentCompleted(
            event.customerId(),
            event.reservationId(),
            event.title(),
            event.userRole()
        );
        notificationService.createAndSendNotification(notification);
    }
}