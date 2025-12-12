package gnu.project.backend.order.event;

import static gnu.project.backend.common.error.ErrorCode.RESERVATION_NOT_FOUND_EXCEPTION;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.entity.OrderDetail;
import gnu.project.backend.order.repository.OrderRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.event.ReservationApprovedEvent;
import gnu.project.backend.reservation.repository.ReservationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationOnReservationApprovedListener {

    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationApproved(final ReservationApprovedEvent event) {
        Long reservationId = event.reservationId();

        if (orderRepository.existsByReservationId(reservationId)) {
            log.info("[OrderCreate:SKIP] reservationId={} already has order", reservationId);
            return;
        }

        Reservation reservation = reservationRepository.findByIdWithAllRelations(reservationId)
            .orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND_EXCEPTION));

        Product product = reservation.getProduct();
        OrderDetail detail = OrderDetail.of(product, 1);
        long original = detail.getLineTotal();

        Order order = Order.fromReservation(
            reservation,
            UUID.randomUUID().toString(),
            original,
            0L,
            original,
            List.of(detail)
        );

        Order saved = orderRepository.save(order);

        log.info("[OrderCreated] orderId={} reservationId={} customerId={}",
            saved.getId(), reservationId, reservation.getCustomer().getId());
    }
}
