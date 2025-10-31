package gnu.project.backend.order.dto.response;

import gnu.project.backend.order.entity.OrderDetail;

public record OrderDetailResponse(
        Long productId,
        String productName,
        Long priceAtPurchase
) {
    public static OrderDetailResponse from(OrderDetail orderDetail) {
        return new OrderDetailResponse(
                orderDetail.getProduct().getId(),
                orderDetail.getProduct().getName(),
                orderDetail.getFinalPrice()
        );
    }
}
