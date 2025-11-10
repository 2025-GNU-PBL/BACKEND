package gnu.project.backend.payment.dto.request;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {}
