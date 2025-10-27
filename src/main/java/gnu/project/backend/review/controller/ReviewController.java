package gnu.project.backend.review.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.review.controller.docs.ReviewDocs;
import gnu.project.backend.review.dto.request.ReviewCreateRequest;
import gnu.project.backend.review.dto.request.ReviewUpdateRequest;
import gnu.project.backend.review.dto.response.ReviewResponse;
import gnu.project.backend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController implements ReviewDocs {

    private final ReviewService reviewService;

    @Override
    @PostMapping(
            value = "/products/{productId}/reviews",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> createReview(
            @PathVariable("productId") final Long productId,
            @Parameter(hidden = true) @Auth final Accessor accessor,
            @Valid @RequestPart("request") final ReviewCreateRequest request,
            @RequestPart(name = "image", required = false) final MultipartFile image
    ) {
        reviewService.createReview(productId, accessor, request, image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<Page<ReviewResponse>> findReviewsByProduct(
            @PathVariable("productId") final Long productId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") final Integer pageSize
    ) {
        return ResponseEntity.ok(
                reviewService.readReviewsByProduct(productId, pageNumber, pageSize)
        );
    }

    @Override
    @GetMapping("/reviews/me")
    public ResponseEntity<Page<ReviewResponse>> readMyReviews(
            @Parameter(hidden = true) @Auth final Accessor accessor,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") final Integer pageSize
    ) {
        return ResponseEntity.ok(
                reviewService.readMyReviews(accessor, pageNumber, pageSize)
        );
    }

    @Override
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> readMyReviewDetail(
            @PathVariable("reviewId") final Long reviewId,
            @Parameter(hidden = true) @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
                reviewService.readMyReviewDetail(reviewId, accessor)
        );
    }

    @Override
    @PutMapping(
            value = "/reviews/{reviewId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> updateReview(
            @PathVariable("reviewId") final Long reviewId,
            @Parameter(hidden = true) @Auth final Accessor accessor,
            @Valid @RequestPart("request") final ReviewUpdateRequest request,
            @RequestPart(name = "image", required = false) final MultipartFile image
    ) {
        reviewService.updateReview(reviewId, accessor, request, image);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("reviewId") final Long reviewId,
            @Parameter(hidden = true) @Auth final Accessor accessor
    ) {
        reviewService.deleteReview(reviewId, accessor);
        return ResponseEntity.noContent().build();
    }
}
