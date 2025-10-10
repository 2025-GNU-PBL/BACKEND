package gnu.project.backend.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelRequest {
    private String paymentKey;
    private String cancelReason;
    private Long cancelAmount;

    public PaymentCancelRequest(String paymentKey, String cancelReason, Long cancelAmount) {
        this.paymentKey = paymentKey;
        this.cancelReason = cancelReason;
        this.cancelAmount = cancelAmount;
    }
}
