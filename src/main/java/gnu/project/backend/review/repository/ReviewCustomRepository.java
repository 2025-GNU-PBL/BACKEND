package gnu.project.backend.review.repository;

import gnu.project.backend.review.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewCustomRepository {

    List<ReviewResponse> searchReviewsByProductId(
            Long productId,
            Pageable pageable
    );

    long countByProductId(Long productId);

    boolean existsByProductIdAndCustomerId(Long productId, Long customerId);

    Optional<ProductRatingStat> getRatingStatByProductId(Long productId);

    List<ReviewResponse> searchReviewsByCustomerId(Long customerId, Pageable pageable);

    long countByCustomerId(Long customerId);

    record ProductRatingStat(
            double avgStar,
            long reviewCount
    ) {}
}
