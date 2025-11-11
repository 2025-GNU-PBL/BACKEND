package gnu.project.backend.payment.dto.request;

public record PaymentCancelRequest(
        String paymentKey,
        String cancelReason
) {}
