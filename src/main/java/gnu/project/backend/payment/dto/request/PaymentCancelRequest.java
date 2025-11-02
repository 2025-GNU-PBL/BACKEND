package gnu.project.backend.payment.dto.request;

public record PaymentCancelRequest(
        String paymentKey,
        String cancelReason,
        Long cancelAmount // 지금은 안 쓸 거면 서비스에서 무시
) {}
