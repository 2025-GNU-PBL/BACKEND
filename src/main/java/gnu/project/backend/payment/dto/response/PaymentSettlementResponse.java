package gnu.project.backend.payment.dto.response;

import java.util.List;

public record PaymentSettlementResponse(
        PaymentSettlementSummaryResponse summary,
        List<PaymentSettlementSummaryItemResponse> items
) {
}
