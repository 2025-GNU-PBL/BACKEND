package gnu.project.backend.payment.dto.response;

public record PaymentSettlementSummaryResponse(
        String ownerName,
        long totalSalesAmount,
        long expectedSettlementAmount,
        int completedCount,
        int cancelCount
) {
}
