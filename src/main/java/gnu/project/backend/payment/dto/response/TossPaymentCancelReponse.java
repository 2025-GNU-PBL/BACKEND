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

    private String paymentKey;       // 결제 키
    private String status;           // 결제 상태 (CANCELED 등)
    private String orderId;          // 주문 ID
    private Long totalAmount;        // 총 결제 금액
    private Long canceledAmount;     // 취소된 금액
    private String cancelReason;     // 취소 사유
    private ZonedDateTime canceledAt; // 취소 시각
    private Card card;               // 카드 결제의 경우 카드 정보 포함 가능

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Card {
        private String company;          // 카드사명
        private String number;           // 카드 번호 (마스킹됨)
        private String ownerType;        // 개인 / 법인 구분
    }

}
