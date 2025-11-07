package gnu.project.backend.order.dto.response;

public record CouponPreviewResponse(
        long originalPrice,
        long discountAmount,
        long totalPrice
) {
}
