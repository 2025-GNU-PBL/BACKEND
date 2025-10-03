package gnu.project.backend.common.enumerated;

public enum OrderStatus {
    WAITING_FOR_PAYMENT, // 결제 대기
    PAID,                // 결제 완료
    CANCELLED            // 주문 취소
}
