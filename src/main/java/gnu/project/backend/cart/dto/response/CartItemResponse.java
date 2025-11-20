package gnu.project.backend.cart.dto.response;

import gnu.project.backend.cart.entity.CartItem;
import gnu.project.backend.product.entity.Product;


public record CartItemResponse(
    Long cartItemId,
    Long productId,
    String productName,
    String bzName,
    Integer price,
    Integer quantity,
    boolean selected,
    String thumbnailUrl
) {

    public static CartItemResponse from(CartItem item) {
        final Product p = item.getProduct();
        return new CartItemResponse(
            item.getId(),
            p.getId(),
            p.getName(),
            p.getOwner().getBzName(),
            p.getPrice(),
            item.getQuantity(),
            item.isSelected(),
            p.getThumbnailUrl()
        );
    }
}
