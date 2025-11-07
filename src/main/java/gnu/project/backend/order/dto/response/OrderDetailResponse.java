package gnu.project.backend.order.dto.response;

import gnu.project.backend.order.entity.OrderDetail;

public record OrderDetailResponse(
        Long productId,
        String productName,
        String thumbnailUrl,
        Long unitPrice,
        Integer quantity,
        Long lineTotal
) {
    public static OrderDetailResponse from(OrderDetail d) {
        return new OrderDetailResponse(
                d.getProduct().getId(),
                d.getProductNameSnapshot(),
                d.getThumbnailSnapshot(),
                d.getUnitPriceAtPurchase(),
                d.getQuantity(),
                d.getLineTotal()
        );
    }
}
