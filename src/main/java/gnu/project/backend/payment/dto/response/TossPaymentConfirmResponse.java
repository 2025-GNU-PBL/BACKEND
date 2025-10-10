package gnu.project.backend.payment.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossPaymentConfirmResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private String method;
    private Long totalAmount;
    private Receipt receipt;
    @JsonProperty("approvedAt")
    private String approvedAt;

    @Getter
    @NoArgsConstructor
    public static class Receipt {
        private String url;
    }

}
