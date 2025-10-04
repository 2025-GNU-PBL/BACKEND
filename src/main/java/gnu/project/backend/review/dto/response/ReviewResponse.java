package gnu.project.backend.review.dto.response;


import gnu.project.backend.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponse {
    private Long id;
    private Long customerId;
    private String title;
    private String customerName;
    private Short star;
    private String comment;
    private String imageUrl;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.customerId = review.getCustomer().getId();
        this.title = review.getTitle();
        this.customerName = review.getCustomer().getName();
        this.star = review.getStar();
        this.comment = review.getComment();
        this.imageUrl = review.getImageUrl();
    }
}


