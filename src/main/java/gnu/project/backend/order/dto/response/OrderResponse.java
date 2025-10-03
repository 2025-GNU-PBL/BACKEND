package gnu.project.backend.order.dto.response;

import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.order.entity.Order;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderResponse {
    private final Long orderId;
    private final String orderCode;
    private final Long totalAmount;
    private final OrderStatus status;
    private final List<OrderDetailResponse> orderDetails;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.orderCode = order.getOrderCode();
        this.totalAmount = order.getTotalPrice();
        this.status = order.getStatus();
        this.orderDetails = order.getOrderDetails().stream()
                .map(OrderDetailResponse::new)
                .toList();
    }
}
