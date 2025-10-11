package gnu.project.backend.payment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.smartcardio.Card;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@ToString
public class TossPaymentCancelReponse {

    private String paymentKey;
    private String status;
    private String orderId;
    private Long totalAmount;
    private Long canceledAmount;
    private String cancelReason;
    private ZonedDateTime canceledAt;
    private Card card;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Card {
        private String company;
        private String number;
        private String ownerType;
    }

}
