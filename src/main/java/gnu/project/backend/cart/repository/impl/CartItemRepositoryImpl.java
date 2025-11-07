package gnu.project.backend.cart.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.cart.entity.CartItem;
import gnu.project.backend.cart.entity.QCart;
import gnu.project.backend.cart.entity.QCartItem;
import gnu.project.backend.cart.repository.CartItemCustomRepository;
import gnu.project.backend.customer.entity.QCustomer;
import gnu.project.backend.product.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemCustomRepository {

    private static final QCart cart = QCart.cart;
    private static final QCartItem cartItem = QCartItem.cartItem;
    private static final QCustomer customer = QCustomer.customer;
    private static final QProduct product = QProduct.product;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItem> findAllByCustomerSocialId(String socialId) {
        return queryFactory
                .selectFrom(cartItem)
                .join(cartItem.cart, cart).fetchJoin()
                .join(cart.customer, customer).fetchJoin()
                .join(cartItem.product, product).fetchJoin()
                .where(customer.oauthInfo.socialId.eq(socialId))
                .orderBy(cartItem.createdAt.desc())
                .fetch();
    }

    @Override
    public List<CartItem> findSelectedByCustomerSocialId(String socialId) {
        return queryFactory
                .selectFrom(cartItem)
                .join(cartItem.cart, cart).fetchJoin()
                .join(cart.customer, customer).fetchJoin()
                .join(cartItem.product, product).fetchJoin()
                .where(
                        customer.oauthInfo.socialId.eq(socialId),
                        cartItem.selected.isTrue()
                )
                .fetch();
    }

    @Override
    public CartItem findSameItem(Long cartId, Long productId, LocalDateTime desireDate) {
        return queryFactory
                .selectFrom(cartItem)
                .where(
                        cartItem.cart.id.eq(cartId),
                        cartItem.product.id.eq(productId),
                        desireDate != null ? cartItem.desireDate.eq(desireDate) : cartItem.desireDate.isNull()
                )
                .fetchFirst();
    }
}
