package gnu.project.backend.payment.dto.response;

import gnu.project.backend.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentRefundResponse {
    private String paymentKey;
    private String orderCode;
    private Long refundAmount;
    private String refundReason;
    private LocalDateTime refundedAt;
    private String status;

    public PaymentRefundResponse(Payment payment) {
        this.paymentKey = payment.getPaymentKey();
        this.orderCode = payment.getOrder().getOrderCode();
        this.refundAmount = payment.getAmount();
        this.refundReason = payment.getCancelReason();
        this.refundedAt = payment.getCanceledAt();
        this.status = String.valueOf(payment.getStatus());
    }
}

