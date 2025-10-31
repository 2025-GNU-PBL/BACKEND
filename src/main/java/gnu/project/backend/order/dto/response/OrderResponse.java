package gnu.project.backend.order.dto.response;

import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.order.entity.Order;

import java.util.List;

public record OrderResponse(
        Long orderId,
        String orderCode,
        Long originalPrice,
        Long discountAmount,
        Long totalAmount,
        OrderStatus status,
        List<OrderDetailResponse> orderDetails
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getOriginalPrice(),
                order.getDiscountAmount(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getOrderDetails().stream()
                        .map(OrderDetailResponse::from)
                        .toList()
        );
    }
}
