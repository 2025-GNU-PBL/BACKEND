package gnu.project.backend.review.entity;


import gnu.project.backend.common.entity.BaseEntity;
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
    @JoinColumn(name = "product_id"/*, nullable = false */)
    private Product product;

    @Column(nullable = false)
    private Short star;

    //Title 추가
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column
    private String imageUrl;

    public static Review createReview(Customer customer, Product product, Short star, String comment, String imageUrl, String title) {
        Review review = new Review();
        review.customer = customer;
        review.product = product;
        review.star = star;
        review.title = title;
        review.comment = comment;
        review.imageUrl = imageUrl;
        return review;
    }


    public void update(Short star, String title,String comment) {
        this.star = star;
        this.title = title;
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

//    public void addImage(String imageUrl){
//        this.imageUrl = imageUrl;
//    }

}
