package gnu.project.backend.cart.entity;


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
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = true)
    private Option option;

    @Column(nullable = true)
    private Integer quantity;

    private LocalDateTime desireDate;
    private String memo;


    public static CartItem create(Cart cart,
                                  Product product,
                                  Option option,
                                  Integer quantity,
                                  LocalDateTime desireDate,
                                  String memo) {
        CartItem cartItem = new CartItem();
        cartItem.cart = cart;
        cartItem.product = product;
        cartItem.option = option;
        cartItem.quantity = quantity;
        cartItem.desireDate = desireDate;
        cartItem.memo = memo;
        return cartItem;
    }

}
