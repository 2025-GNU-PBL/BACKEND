package gnu.project.backend.order.dto.response;

import gnu.project.backend.order.entity.OrderDetail;
import lombok.Getter;

@Getter
public class OrderDetailResponse {

    private final Long productId;
    private final String productName;
    private final Long priceAtPurchase;

    public OrderDetailResponse(OrderDetail orderDetail) {
        this.productId = orderDetail.getProduct().getId();
        this.productName = orderDetail.getProduct().getName();
        this.priceAtPurchase = orderDetail.getFinalPrice();
    }
}
