package gnu.project.backend.review.entity;


import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.common.enumerated.ReviewSatisfaction;
import gnu.project.backend.common.mapper.StringListConverter;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
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

    @Column(name = "image_urls", columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "timesatisfaction", length = 20)
    private ReviewSatisfaction timeSatisfaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "picsatisfaction", length = 20)
    private ReviewSatisfaction picSatisfaction;

    public static Review create(
        final Customer customer,
        final Product product,
        final Short star,
        final String title,
        final String comment,
        final List<String> imageUrls,
        final ReviewSatisfaction timeSatisfaction,
        final ReviewSatisfaction picSatisfaction
    ) {
        final Review review = new Review();
        review.customer = customer;
        review.product = product;
        review.star = star;
        review.title = title;
        review.comment = comment;
        review.imageUrls = imageUrls;
        review.timeSatisfaction = timeSatisfaction;
        review.picSatisfaction = picSatisfaction;
        return review;
    }

}
