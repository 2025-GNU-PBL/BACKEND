package gnu.project.backend.cart.dto.response;

import gnu.project.backend.cart.entity.CartItem;
import gnu.project.backend.product.entity.Product;

import java.time.LocalDateTime;

public record CartItemResponse(
        Long cartItemId,
        Long productId,
        String productName,
        Integer price,
        Integer quantity,
        boolean selected,
        String thumbnailUrl,
        LocalDateTime desireDate
) {
    public static CartItemResponse from(CartItem item) {
        final Product p = item.getProduct();
        return new CartItemResponse(
                item.getId(),
                p.getId(),
                p.getName(),
                p.getPrice(),
                item.getQuantity(),
                item.isSelected(),
                p.getThumbnailUrl(),
                item.getDesireDate()
        );
    }
}
