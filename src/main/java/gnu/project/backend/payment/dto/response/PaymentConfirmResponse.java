package gnu.project.backend.payment.dto.response;

import gnu.project.backend.payment.entity.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class PaymentConfirmResponse {
    private final String orderId;
    private final String paymentMethod;
    private final Long amount;
    private final String receiptUrl;

    public PaymentConfirmResponse(Payment payment) {
        this.orderId = payment.getOrder().getOrderCode();
        this.paymentMethod = payment.getPaymentMethod();
        this.amount = payment.getAmount();
        this.receiptUrl = payment.getReceiptUrl();
    }


}
