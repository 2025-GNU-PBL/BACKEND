package gnu.project.backend.cart.entity;


import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.product.entity.Product;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "cart_item",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cart_item_merge_key",
                columnNames = {"cart_id", "product_id"}
        ),
        indexes = {
                @Index(name = "idx_cart_item_cart", columnList = "cart_id"),
                @Index(name = "idx_cart_item_product", columnList = "product_id"),
                @Index(name = "idx_cart_item_selected", columnList = "selected")
        }
)
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;


    @Column(nullable = false)
    private boolean selected = true;

    private CartItem(
        Cart cart,
        Product product,
        Integer quantity
    ) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.selected = true;
    }

    public static CartItem create(
        Cart cart,
        Product product,
        Integer quantity
    ) {
        return new CartItem(cart, product, quantity);
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateSelected(boolean selected) {
        this.selected = selected;
    }

}
