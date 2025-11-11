package gnu.project.backend.payment.dto.response;

import gnu.project.backend.common.enumerated.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentSettlementSummaryItemResponse(
        String orderCode,           // 주문 코드 (디테일 화면 진입용)
        String customerName,        // 고객 이름 (예: 김지현)
        Long amount,                // 결제 금액
        PaymentStatus status,       // DONE / CANCELED / REFUND_COMPLETED 등
        LocalDateTime approvedAt    // 결제 승인 시각 (리스트에 날짜/시간 표기용)
) {
}
