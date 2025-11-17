package gnu.project.backend.payment.dto.response;

import gnu.project.backend.common.enumerated.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentSettlementSummaryItemResponse(
        String paymentKey,
        String orderCode,
        String customerName,
        Long amount,
        PaymentStatus status,
        LocalDateTime approvedAt
) {
}
