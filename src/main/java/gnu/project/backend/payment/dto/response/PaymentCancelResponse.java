package gnu.project.backend.payment.dto.response;

import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.payment.entity.Payment;

import java.time.LocalDateTime;

public record PaymentCancelResponse(
        String paymentKey,
        String orderCode,
        String shopName,
        String productName,
        PaymentStatus status,
        String cancelReason,
        LocalDateTime canceledAt
) {
    public static PaymentCancelResponse from(Payment p) {
        return new PaymentCancelResponse(
                p.getPaymentKey(),
                p.getOrder().getOrderCode(),
                p.getOrder().getShopName(),
                p.getOrder().getMainProductName(),
                p.getStatus(),
                p.getCancelReason(),
                p.getCanceledAt()
        );
    }
}
