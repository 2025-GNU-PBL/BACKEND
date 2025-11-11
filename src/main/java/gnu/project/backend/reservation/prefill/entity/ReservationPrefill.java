package gnu.project.backend.reservation.prefill.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.product.entity.Product;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reservation_prefill",
        indexes = {
                @Index(name = "idx_prefill_customer", columnList = "customer_id"),
                @Index(name = "idx_prefill_expires", columnList = "expires_at"),
                @Index(name = "idx_prefill_consumed", columnList = "consumed")
        }
)
public class ReservationPrefill extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean consumed = false;

    private ReservationPrefill(
            Customer customer,
            Product product,
            Integer quantity,
            LocalDateTime expiresAt
    ) {
        this.customer = customer;
        this.product = product;
        this.quantity = (quantity != null && quantity > 0) ? quantity : 1;
        this.expiresAt = expiresAt;
    }

    public static ReservationPrefill create(
            Customer customer,
            Product product,
            Integer quantity,
            LocalDateTime expiresAt
    ) {
        return new ReservationPrefill(customer, product, quantity, expiresAt);
    }

    public void consume() { this.consumed = true; }

    public boolean isExpired(LocalDateTime now) { return now.isAfter(this.expiresAt); }
}
