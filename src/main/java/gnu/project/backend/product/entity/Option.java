package gnu.project.backend.product.entity;


import gnu.project.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Option extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String name;


    private Option(
        final Product product,
        final String detail,
        final Integer price,
        final String name
    ) {
        this.product = product;
        this.detail = detail;
        this.price = price;
        this.name = name;
    }

    public static Option ofCreate(
        final Product product,
        final String detail,
        final Integer price,
        final String name
    ) {
        return new Option(product, detail, price, name);
    }
}
