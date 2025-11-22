package gnu.project.backend.payment.service;

import static gnu.project.backend.common.error.ErrorCode.ORDER_NOT_FOUND;
import static gnu.project.backend.common.error.ErrorCode.PAYMENT_ACCESS_DENIED;
import static gnu.project.backend.common.error.ErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static gnu.project.backend.common.error.ErrorCode.PAYMENT_NOT_FOUND;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.notification.event.dto.PaymentApprovedEvent;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.repository.OrderRepository;
import gnu.project.backend.payment.dto.request.PaymentConfirmRequest;
import gnu.project.backend.payment.dto.response.PaymentConfirmResponse;
import gnu.project.backend.payment.dto.response.TossPaymentConfirmResponse;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConfirmService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TossPaymentClient tossPaymentClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentConfirmResponse confirm(Accessor accessor, PaymentConfirmRequest request) {

        Order order = orderRepository.findByOrderCodeWithDetails(request.orderCode())
            .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        String customerSocialId = order.getCustomerSocialId();
        if (!accessor.getSocialId().equals(customerSocialId)) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        if (!order.getTotalPrice().equals(request.amount())) {
            throw new BusinessException(PAYMENT_AMOUNT_MISMATCH);
        }

        TossPaymentConfirmResponse toss = tossPaymentClient.confirmPayment(
            request.paymentKey(),
            request.orderCode(),
            request.amount()
        );
        OrderStatus beforeStatus = order.getStatus();
        Payment payment = savePaymentAndUpdateOrder(order, toss);

        if (beforeStatus != OrderStatus.PAID && order.getStatus() == OrderStatus.PAID) {
            applicationEventPublisher.publishEvent(
                new PaymentApprovedEvent(
                    order.getCustomer().getId(),
                    order.getReservation().getId(),
                    order.getReservation().getTitle()
                )
            );
        }

        log.info("결제 승인 완료: orderCode={}", order.getOrderCode());
        return PaymentConfirmResponse.from(payment);
    }

    @Transactional
    protected Payment savePaymentAndUpdateOrder(Order order, TossPaymentConfirmResponse toss) {
        // idem 보호
        if (order.getStatus() == OrderStatus.PAID) {
            return paymentRepository.findWithOrderAndDetailsByPaymentKey(toss.getPaymentKey())
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));
        }

        LocalDateTime approvedAt = null;
        if (toss.getApprovedAt() != null) {
            ZonedDateTime zdt = ZonedDateTime.parse(
                toss.getApprovedAt(),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            );
            approvedAt = zdt.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }

        Payment payment = Payment.create(
            order,
            toss.getPaymentKey(),
            "tosspayments",
            toss.getMethod(),
            toss.getTotalAmount(),
            toss.getStatus(),
            approvedAt,
            toss.getReceipt() != null ? toss.getReceipt().getUrl() : null
        );

        order.updateStatus(mapOrderStatus(payment.getStatus()));

        paymentRepository.save(payment);
        orderRepository.save(order);
        return payment;
    }

    private OrderStatus mapOrderStatus(PaymentStatus status) {
        return switch (status) {
            case DONE -> OrderStatus.PAID;
            case CANCELED -> OrderStatus.CANCELED;
            default -> OrderStatus.PAYMENT_FAILED;
        };
    }
}
