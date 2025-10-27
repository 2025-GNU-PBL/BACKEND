package gnu.project.backend.review.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.common.exception.AuthException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ProductRepository;
import gnu.project.backend.review.dto.request.ReviewCreateRequest;
import gnu.project.backend.review.dto.request.ReviewUpdateRequest;
import gnu.project.backend.review.dto.response.ReviewResponse;
import gnu.project.backend.review.entity.Review;
import gnu.project.backend.review.provider.ReviewImageProvider;
import gnu.project.backend.review.repository.ReviewCustomRepository.ProductRatingStat;
import gnu.project.backend.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static gnu.project.backend.common.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ReviewImageProvider reviewImageProvider;


    private Customer getCurrentCustomer(final Accessor accessor) {
        return customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));
    }

    private Product getProductOrThrow(final Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND_EXCEPTION));
    }

    private Review getReviewOrThrow(final Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(REVIEW_NOT_FOUND));
    }

    private void validateOwner(final Review review, final Customer currentCustomer) {
        if (!review.getCustomer().getId().equals(currentCustomer.getId())) {
            throw new AuthException(REVIEW_FORBIDDEN);
        }
    }

    private void recalcAndApplyProductRating(final Product product) {

        final ProductRatingStat stat = reviewRepository
                .getRatingStatByProductId(product.getId())
                .orElse(new ProductRatingStat(0.0, 0L));

        product.updateRating(stat.avgStar(), (int) stat.reviewCount());
    }

    private void validatePurchase(final Customer customer, final Product product) {
        // boolean purchased = reservationRepository.existsByCustomerIdAndProductIdAndStatus(
        //        customer.getId(), product.getId(), "CONFIRMED"
        // );
        boolean purchased = true; // 지금은 임시 허용

        if (!purchased) {
            throw new BusinessException(REVIEW_NOT_ELIGIBLE);
        }
    }



    @Transactional
    public void createReview(
            final Long productId,
            final Accessor accessor,
            final ReviewCreateRequest request,
            final MultipartFile image
    ) {
        final Customer customer = getCurrentCustomer(accessor);
        final Product product = getProductOrThrow(productId);

        validatePurchase(customer, product); // <- 일단은 항상 통과


        if (reviewRepository.existsByProductIdAndCustomerId(productId, customer.getId())) {
            throw new BusinessException(REVIEW_DUPLICATE);
        }

        final String imageUrl = reviewImageProvider.uploadReviewImage(
                product,
                customer.getSocialId(),
                image
        );

        final Review review = Review.create(
                customer,
                product,
                request.star(),
                request.title(),
                request.comment(),
                imageUrl
        );

        reviewRepository.save(review);

        recalcAndApplyProductRating(product);
    }


    @Transactional(readOnly = true)
    public Page<ReviewResponse> readReviewsByProduct(
            final Long productId,
            final Integer pageNumber,
            final Integer pageSize
    ) {
        final Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        final long totalElements = reviewRepository.countByProductId(productId);

        final List<ReviewResponse> pageContent =
                reviewRepository.searchReviewsByProductId(productId, pageable);

        return new PageImpl<>(
                pageContent,
                pageable,
                totalElements
        );
    }

    @Transactional
    public void updateReview(
            final Long reviewId,
            final Accessor accessor,
            final ReviewUpdateRequest request,
            final MultipartFile newImage
    ) {
        final Review review = getReviewOrThrow(reviewId);
        final Customer currentCustomer = getCurrentCustomer(accessor);

        validateOwner(review, currentCustomer);

        String imageUrl = review.getImageUrl();
        if (newImage != null && !newImage.isEmpty()) {
            imageUrl = reviewImageProvider.uploadReviewImage(
                    review.getProduct(),
                    currentCustomer.getSocialId(),
                    newImage
            );
        }

        review.update(
                request.star(),
                request.title(),
                request.comment(),
                imageUrl
        );

        recalcAndApplyProductRating(review.getProduct());
    }


    @Transactional
    public void deleteReview(
            final Long reviewId,
            final Accessor accessor
    ) {
        final Review review = getReviewOrThrow(reviewId);
        final Customer currentCustomer = getCurrentCustomer(accessor);

        validateOwner(review, currentCustomer);

        reviewRepository.delete(review);

        recalcAndApplyProductRating(review.getProduct());
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> readMyReviews(
            final Accessor accessor,
            final Integer pageNumber,
            final Integer pageSize
    ) {
        final Customer me = getCurrentCustomer(accessor);

        final Pageable pageable = PageRequest.of(
                pageNumber - 1,
                pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        final long totalElements = reviewRepository.countByCustomerId(me.getId());

        final List<ReviewResponse> pageContent =
                reviewRepository.searchReviewsByCustomerId(me.getId(), pageable);

        return new PageImpl<>(
                pageContent,
                pageable,
                totalElements
        );
    }

    @Transactional(readOnly = true)
    public ReviewResponse readMyReviewDetail(
            final Long reviewId,
            final Accessor accessor
    ) {
        final Review review = getReviewOrThrow(reviewId);
        final Customer me = getCurrentCustomer(accessor);

        validateOwner(review, me);

        return ReviewResponse.from(review);
    }






}
