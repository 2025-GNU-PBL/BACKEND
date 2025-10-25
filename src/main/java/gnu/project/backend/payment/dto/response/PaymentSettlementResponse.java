package gnu.project.backend.payment.dto.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PaymentSettlementResponse {
    private final String productName;
    private final Long totalAmount;
    private final Long settledAmount; // 환불, 취소 반영 후 실제 정산 금액
    private final Integer count;      // 결제 건수
    private final String orderCode;
    private final Long amount;

    @QueryProjection
    public PaymentSettlementResponse(String productName, Long totalAmount, Long settledAmount, Integer count, String orderCode, Long amount) {
        this.productName = productName;
        this.totalAmount = totalAmount;
        this.settledAmount = settledAmount;
        this.count = count;
        this.orderCode = orderCode;
        this.amount = amount;
    }
}
