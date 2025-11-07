package gnu.project.backend.cart.entity;


import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "cart",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_customer", columnNames = "customer_id")
)
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    private Cart(Customer customer) {
        this.customer = customer;
    }

    public static Cart create(Customer customer) {
        return new Cart(customer);
    }
}
