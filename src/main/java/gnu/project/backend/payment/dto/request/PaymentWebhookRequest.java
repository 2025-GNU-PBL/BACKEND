package gnu.project.backend.payment.dto.request;


import gnu.project.backend.common.enumerated.PaymentStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PaymentWebhookRequest {
    private String eventType; // 이벤트 타입 (예: "PAYMENT_STATUS_CHANGED")

    private Data data;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Data {
        private String paymentKey;
        private String orderId;
        private PaymentStatus status;
        // 웹훅 이벤트에 따라 더 많은 필드가 올 수 있습니다.
    }
}
