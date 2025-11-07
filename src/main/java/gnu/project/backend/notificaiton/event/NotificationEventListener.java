package gnu.project.backend.notificaiton.event;

import gnu.project.backend.notificaiton.entity.Notification;
import gnu.project.backend.notificaiton.repository.NotificationRepository;
import gnu.project.backend.notificaiton.service.SseEmitterService;
import gnu.project.backend.reservation.event.ReservationApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationApproved(ReservationApprovedEvent event) {
        final Notification notification = Notification.createPaymentNotification(
            event.customerId(),
            event.reservationId(),
            event.title(),
            String.format("/payment?reservationId=%d", event.reservationId())
        );
        notificationRepository.save(notification);
        sseEmitterService.sendNotification(event.customerId(), notification);
    }
}