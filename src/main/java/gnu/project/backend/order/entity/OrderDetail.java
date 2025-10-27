package gnu.project.backend.order.entity;

import gnu.project.backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Order_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "final_price")
    private Long finalPrice;

    public static OrderDetail create(Product product) {
        OrderDetail detail = new OrderDetail();
        detail.product = product;
        detail.finalPrice = Long.valueOf(product.getPrice()); // Product의 가격 타입에 맞춰 변환
        return detail;
    }
}
