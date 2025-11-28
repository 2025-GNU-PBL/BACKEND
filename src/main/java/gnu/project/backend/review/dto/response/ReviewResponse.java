package gnu.project.backend.review.dto.response;

import gnu.project.backend.common.enumerated.ReviewSatisfaction;
import gnu.project.backend.review.entity.Review;

import java.util.List;

public record ReviewResponse(
        Long id,
        Long customerId,
        String customerName,
        Long productId,
        Short star,
        String title,
        String comment,
        List<String> imageUrls,
        ReviewSatisfaction timeSatisfaction,
        ReviewSatisfaction picSatisfaction
) {
    public static ReviewResponse from(final Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getCustomer().getId(),
                review.getCustomer().getName(),
                review.getProduct().getId(),
                review.getStar(),
                review.getTitle(),
                review.getComment(),
                review.getImageUrls(),
                review.getTimeSatisfaction(),
                review.getPicSatisfaction()

        );
    }
}