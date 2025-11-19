package gnu.project.backend.payment.dto.response;

import gnu.project.backend.common.enumerated.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentDetailResponse(
        String paymentKey,
        String orderCode,

        String shopName,
        String productName,
        String thumbnailUrl,

        Long originalPrice,
        Long discountAmount,
        Long totalPrice,
        Long paidAmount,

        PaymentStatus status,
        LocalDateTime approvedAt,
        LocalDateTime canceledAt,
        String cancelReason,
        String receiptUrl,
        String paymentMethod,
        String pgProvider,

        String rejectReason,
        LocalDateTime rejectedAt
) {
}
