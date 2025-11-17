package gnu.project.backend.payment.dto.response;

import gnu.project.backend.payment.entity.Payment;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderCode,
        String paymentMethod,
        Long amount,
        String receiptUrl
) {
    public static PaymentConfirmResponse from(Payment p) {
        return new PaymentConfirmResponse(
                p.getPaymentKey(),
                p.getOrder().getOrderCode(),
                p.getPaymentMethod(),
                p.getAmount(),
                p.getReceiptUrl()
        );
    }
}
