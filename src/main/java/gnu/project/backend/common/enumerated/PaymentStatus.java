package gnu.project.backend.common.enumerated;


public enum PaymentStatus {
    READY,              // 결제 요청 완료 (대기)
    IN_PROGRESS,        // 결제 진행 중
    DONE,               // 결제 완료
    CANCELED,
    CANCEL_REQUESTED,
    REFUND_COMPLETED,   // 환불 완료됨
    FAILED;



    public static PaymentStatus fromString(String status) {
        if (status == null) {
            return FAILED;
        }
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return FAILED; // 안전장치
        }
    }
}
