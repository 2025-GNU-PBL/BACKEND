package gnu.project.backend.cart.dto.response;

import gnu.project.backend.cart.entity.CartItem;
import lombok.Getter;

@Getter
public class CartItemResponse {

    private final Long cartItemId;
    private final Long productId;
    private final String productName;
    private final int price;
    private final int quantity;
    private final String image_url;

    public CartItemResponse(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getName();
        this.price = cartItem.getProduct().getPrice();
        this.quantity=cartItem.getQuantity();
        this.image_url = cartItem.getProduct().getImages().toString();
    }

}
