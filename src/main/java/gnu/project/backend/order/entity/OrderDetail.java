package gnu.project.backend.order.entity;

import gnu.project.backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 주문 시점 확정 가격
    @Column(name = "final_price", nullable = false)
    private Long finalPrice;

    public static OrderDetail of(Product product, Long finalPrice) {
        OrderDetail d = new OrderDetail();
        d.product = product;
        d.finalPrice = finalPrice != null ? finalPrice : product.getPrice().longValue();
        return d;
    }
}
