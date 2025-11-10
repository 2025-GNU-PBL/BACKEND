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
                d.getProduct() != null ? d.getProduct().getId() : null,
                d.getDisplayProductName(),
                d.getDisplayThumbnailUrl(),
                d.getDisplayUnitPrice(),
                d.getQuantity(),
                d.getLineTotal()
        );
    }
}
