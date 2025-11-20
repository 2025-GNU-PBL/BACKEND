package gnu.project.backend.review.entity;


import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.ReviewSatisfaction;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id" , nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Short star;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "satisfaction", length = 20)
    private ReviewSatisfaction satisfaction;

    public static Review create(
            final Customer customer,
            final Product product,
            final Short star,
            final String title,
            final String comment,
            final String imageUrl,
            final ReviewSatisfaction satisfaction
    ) {
        final Review review = new Review();
        review.customer = customer;
        review.product = product;
        review.star = star;
        review.title = title;
        review.comment = comment;
        review.imageUrl = imageUrl;
        review.satisfaction = satisfaction;
        return review;
    }

}
