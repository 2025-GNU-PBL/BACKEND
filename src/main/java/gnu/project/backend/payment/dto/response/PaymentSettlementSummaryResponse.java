package gnu.project.backend.payment.dto.response;

public record PaymentSettlementSummaryResponse(
        String ownerName,
        long totalSalesAmount,       // 누적 총 매출
        long expectedSettlementAmount, // 정산 예정 금액(당장은 total과 동일하게 내려줘도 됨)
        int completedCount,           // 완료 건수 (DONE 기준)
        int cancelCount
) {
}
