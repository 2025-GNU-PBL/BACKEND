package gnu.project.backend.review.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.review.dto.request.ReviewCreateRequest;
import gnu.project.backend.review.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(
        name = "Review API",
        description = "상품 리뷰 CRUD API"
)
public interface ReviewDocs {

    @Operation(
            summary = "리뷰 작성",
            description = "특정 상품(productId)에 대해 고객이 리뷰를 작성한다. request(JSON) + image(file) 멀티파트 업로드."
    )
    @PostMapping(
            value = "/products/{productId}/reviews",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<Void> createReview(
            @PathVariable("productId") Long productId,
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestPart("request") ReviewCreateRequest request,
            @RequestPart(name = "image", required = false) List<MultipartFile> images
    );

    @Operation(
            summary = "상품 리뷰 목록 조회",
            description = "특정 상품(productId)에 달린 리뷰들을 조회한다. 페이징 지원."
    )
    @GetMapping("/products/{productId}/reviews")
    ResponseEntity<Page<ReviewResponse>> findReviewsByProduct(
            @PathVariable("productId") Long productId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    );

    @Operation(
            summary = "내 리뷰 목록 조회",
            description = "현재 로그인한 고객이 쓴 리뷰 목록을 페이징으로 조회한다 (마이페이지)."
    )
    @GetMapping("/reviews/me")
    ResponseEntity<Page<ReviewResponse>> readMyReviews(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    );

    @Operation(
            summary = "내 리뷰 단건 조회",
            description = "수정 화면 진입용. 내가 쓴 특정 reviewId의 상세를 가져온다."
    )
    @GetMapping("/reviews/{reviewId}")
    ResponseEntity<ReviewResponse> readMyReviewDetail(
            @PathVariable("reviewId") Long reviewId,
            @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(
            summary = "리뷰 삭제",
            description = "본인이 작성한 리뷰만 삭제할 수 있다."
    )
    @DeleteMapping("/reviews/{reviewId}")
    ResponseEntity<Void> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @Parameter(hidden = true) @Auth Accessor accessor
    );



}
