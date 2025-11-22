package gnu.project.backend.payment.dto.response;

import gnu.project.backend.common.enumerated.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentListResponse(
        String paymentKey,
        String orderCode,
        String shopName,
        String productName,
        String thumbnailUrl,
        Long productId,
        Long amount,
        PaymentStatus status,
        LocalDateTime approvedAt
) {
}
