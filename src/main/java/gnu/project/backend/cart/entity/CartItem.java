package gnu.project.backend.cart.entity;


import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option option;   // 옵션 없을 수도 있음

    @Column(nullable = false)
    private Integer quantity;

    private LocalDateTime desireDate;

    @Column(length = 500)
    private String memo;

    @Column(nullable = false)
    private boolean selected = true;    // 장바구니 들어오면 기본 선택

    private CartItem(
            Cart cart,
            Product product,
            Option option,
            Integer quantity,
            LocalDateTime desireDate,
            String memo
    ) {
        this.cart = cart;
        this.product = product;
        this.option = option;
        this.quantity = quantity;
        this.desireDate = desireDate;
        this.memo = memo;
        this.selected = true;
    }

    public static CartItem create(
            Cart cart,
            Product product,
            Option option,
            Integer quantity,
            LocalDateTime desireDate,
            String memo
    ) {
        return new CartItem(cart, product, option, quantity, desireDate, memo);
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateSelected(boolean selected) {
        this.selected = selected;
    }
}
