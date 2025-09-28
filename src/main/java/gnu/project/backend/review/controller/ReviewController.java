package gnu.project.backend.review.controller;


import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.review.dto.request.ReviewCreateRequest;
import gnu.project.backend.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping
    public ResponseEntity<Void> createReview(//@PathVariable Long productId,
                                             @Auth final Accessor accessor,
                                             @RequestBody final ReviewCreateRequest request   ) {
        //reviewService.createReview(producId, accessor, request);
        reviewService.createReview(accessor, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
}
