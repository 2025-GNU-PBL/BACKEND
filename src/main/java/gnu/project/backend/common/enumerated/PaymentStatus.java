package gnu.project.backend.common.enumerated;


public enum PaymentStatus {
    DONE,
    CANCELED,
    CANCEL_REQUESTED,
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
