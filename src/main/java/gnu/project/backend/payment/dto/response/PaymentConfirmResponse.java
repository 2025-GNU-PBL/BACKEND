package gnu.project.backend.payment.dto.response;

import gnu.project.backend.payment.entity.Payment;

public record PaymentConfirmResponse(
        String orderId,
        String paymentMethod,
        Long amount,
        String receiptUrl
) {
    public static PaymentConfirmResponse from(Payment p) {
        return new PaymentConfirmResponse(
                p.getOrder().getOrderCode(),
                p.getPaymentMethod(),
                p.getAmount(),
                p.getReceiptUrl()
        );
    }
}
