package gnu.project.backend.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentConfirmRequest {
    private String paymentKey;
    private String orderId;
    private Long amount;
}
