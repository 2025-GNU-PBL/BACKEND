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

    @Column(name = "product_name_snapshot", nullable = false)
    private String productNameSnapshot;

    @Column(name = "thumbnail_snapshot")
    private String thumbnailSnapshot;

    @Column(name = "unit_price_at_purchase", nullable = false)
    private Long unitPriceAtPurchase;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "line_total", nullable = false)
    private Long lineTotal;

    public static OrderDetail of(Product product, int quantity) {
        OrderDetail d = new OrderDetail();
        d.product = product;
        d.productNameSnapshot = product.getName();
        d.thumbnailSnapshot = product.getThumbnailUrl();
        d.unitPriceAtPurchase = product.getPrice().longValue();
        d.quantity = Math.max(1, quantity);
        d.lineTotal = d.unitPriceAtPurchase * d.quantity;
        return d;
    }

    public String getDisplayProductName() {
        if (productNameSnapshot != null) return productNameSnapshot;
        if (product != null) return product.getName();
        return null;
    }

    public String getDisplayThumbnailUrl() {
        if (thumbnailSnapshot != null) return thumbnailSnapshot;
        if (product != null) return product.getThumbnailUrl();
        return null;
    }
    public Long getDisplayUnitPrice() {
        if (unitPriceAtPurchase != null) return unitPriceAtPurchase;
        if (product != null && product.getPrice() != null) {
            return product.getPrice().longValue();
        }
        return null;
    }
}
