package gnu.project.backend.payment.dto.response;

import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.payment.entity.Payment;

import java.time.LocalDateTime;

public record PaymentCancelResponse(
        String paymentKey,
        String orderCode,
        String shopName,
        String productName,
        Long originalPrice,
        Long discountAmount,
        Long refundAmount,
        PaymentStatus status,
        LocalDateTime requestedAt,
        String cancelReason,
        LocalDateTime canceledAt,
        String rejectReason,
        LocalDateTime rejectedAt
) {
    public static PaymentCancelResponse from(Payment p) {
        return new PaymentCancelResponse(
                p.getPaymentKey(),
                p.getOrder().getOrderCode(),
                p.getOrder().getShopName(),
                p.getOrder().getMainProductName(),
                p.getOrder().getOriginalPrice(),
                p.getOrder().getDiscountAmount(),
                p.getOrder().getTotalPrice(),
                p.getStatus(),
                p.getRequestedAt(),
                p.getCancelReason(),
                p.getCanceledAt(),
                p.getCancelRejectReason(),
                p.getCancelRejectAt()
        );
    }
}
