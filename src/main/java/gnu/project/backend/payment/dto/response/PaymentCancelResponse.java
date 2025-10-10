package gnu.project.backend.payment.dto.response;


import gnu.project.backend.payment.entity.Payment;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class PaymentCancelResponse {

    private String orderCode;
    private String status;
    private String cancelReason;
    private LocalDateTime canceledAt;

    public PaymentCancelResponse(Payment payment) {
        this.orderCode = payment.getOrder().getOrderCode();
        this.status = payment.getStatus();
        this.cancelReason = payment.getCancelReason();
        this.canceledAt = payment.getCanceledAt();
    }


}
