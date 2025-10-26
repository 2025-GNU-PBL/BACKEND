package gnu.project.backend.review.service;


import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.AuthException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ImageRepository;
import gnu.project.backend.product.repository.ProductRepository;
import gnu.project.backend.review.dto.request.ReviewCreateRequest;
import gnu.project.backend.review.dto.request.ReviewUpdateRequest;
import gnu.project.backend.review.dto.response.ReviewResponse;
import gnu.project.backend.review.entity.Review;
import gnu.project.backend.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static gnu.project.backend.common.error.ErrorCode.AUTH_FORBIDDEN;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    //private  final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final Executor imageUploadExecutor = Executors.newFixedThreadPool(10);


    //Review 생성 로직 추후 고도화
    public void createReview(/*Long productId,*/ Accessor accessor, ReviewCreateRequest request) {
        Customer customer = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new IllegalArgumentException("고객님을 찾지 못했습니다."));

        // Product product = productRepository.findById(productId).orElseTrow(...);
        // TODO : 고객이 ORDER 연동해서 상품구매 유무 검토로직 구현  및 사진 업로드

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


    //리뷰 조회
    @Transactional(readOnly = true)
    public List<ReviewResponse> findReviewsByProduct (Long productId){
        return reviewRepository.findByProduct_Id(productId).stream().map(ReviewResponse::new).toList();
    }

    //Update
    public void updateReview(Long reviewId, Accessor accessor, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾지 못했습니다."));

        Customer currentCustomer = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        if (!review.getCustomer().getId().equals(currentCustomer.getId())) {
            throw new AuthException(AUTH_FORBIDDEN);

        }
        review.update(request.getStar(), request.getComment(), request.getImageUrl());
    }

    public void deleteReview(Long reviewId, Accessor accessor) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾지 못했습니다."));

        Customer currentCustomer = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        if (!review.getCustomer().getId().equals(currentCustomer.getId())) {
            throw new AuthException(AUTH_FORBIDDEN);

        }

        reviewRepository.delete(review);
    }

}
