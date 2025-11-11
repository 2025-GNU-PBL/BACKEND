package gnu.project.backend.cart.repository;

import gnu.project.backend.cart.entity.CartItem;

import java.util.List;

public interface CartItemCustomRepository {

    List<CartItem> findAllByCustomerSocialId(String socialId);

    List<CartItem> findSelectedByCustomerSocialId(String socialId);

    CartItem findSameItem(
            Long cartId,
            Long productId
    );
}
