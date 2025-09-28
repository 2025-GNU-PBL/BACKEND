package gnu.project.backend.review.service;


import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.review.dto.request.ReviewCreateRequest;
import gnu.project.backend.review.entity.Review;
import gnu.project.backend.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    // private final ProductRepostory productRepostory;
    private final CustomerRepository customerRepository;


    public void createReview(/*Long productId,*/ Accessor accessor, ReviewCreateRequest request) {
        Customer customer = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new IllegalArgumentException("고객님을 찾지 못했습니다."));

        // Product product = productRepository.findById(productId).orElseTrow(...);

        Product product = null;

        Review review = Review.createReview(
                customer,
                product,
                request.getStar(),
                request.getComment(),
                request.getTitle(),
                request.getImageUrl()
        );
        reviewRepository.save(review);
    }

    //TODO : 후기 수저 및 삭제 추가
}
