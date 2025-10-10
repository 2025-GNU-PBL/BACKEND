package gnu.project.backend.payment.dto.request;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString // 로그 출력 시 확인하기 편하도록 추가
public class PaymentWebhookRequest {
    private String eventType; // 이벤트 타입 (예: "PAYMENT_STATUS_CHANGED")

    private Data data;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Data {
        private String paymentKey;
        private String orderId;
        private String status;
        // 웹훅 이벤트에 따라 더 많은 필드가 올 수 있습니다.
    }
}
