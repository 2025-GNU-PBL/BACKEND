package gnu.project.backend.cart.dto.response;

import java.util.List;

public record CartSummaryResponse(
        List<CartItemResponse> items,
        Integer totalProductAmount,
        Integer totalDiscountAmount,
        Integer paymentAmount
) {
}
