package gnu.project.backend.review.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.review.dto.response.ReviewResponse;
import gnu.project.backend.review.entity.QReview;
import gnu.project.backend.customer.entity.QCustomer;
import gnu.project.backend.review.entity.Review;
import gnu.project.backend.review.repository.ReviewCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    private static final QReview review = QReview.review;
    private static final QCustomer customer = QCustomer.customer;

    @Override
    public List<ReviewResponse> searchReviewsByProductId(
            final Long productId,
            final Pageable pageable
    ) {
        final List<Review> reviews = queryFactory
                .selectFrom(review)
                .join(review.customer, customer).fetchJoin()
                .where(
                        review.product.id.eq(productId)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return reviews.stream()
                .map(ReviewResponse::from)
                .toList();
    }


    @Override
    public long countByProductId(final Long productId) {
        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.product.id.eq(productId)
                )
                .fetchOne();
        return (count == null ? 0L : count);
    }

    @Override
    public boolean existsByProductIdAndCustomerId(
            final Long productId,
            final Long customerId
    ) {
        Integer result = queryFactory
                .selectOne()
                .from(review)
                .where(
                        review.product.id.eq(productId),
                        review.customer.id.eq(customerId)
                )
                .fetchFirst();
        return result != null;
    }

    @Override
    public Optional<ProductRatingStat> getRatingStatByProductId(final Long productId) {
        final Double avgStar = queryFactory
                .select(review.star.avg())
                .from(review)
                .where(
                        review.product.id.eq(productId)
                )
                .fetchOne();

        final Long cnt = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.product.id.eq(productId)
                )
                .fetchOne();

        if (cnt == null || cnt == 0L || avgStar == null) {
            return Optional.of(new ProductRatingStat(0.0, 0L));
        }
        return Optional.of(new ProductRatingStat(avgStar, cnt));
    }

    @Override
    public List<ReviewResponse> searchReviewsByCustomerId(
            final Long customerId,
            final Pageable pageable
    ) {
        final List<Review> reviews = queryFactory
                .selectFrom(review)
                .join(review.customer, customer).fetchJoin()
                .where(
                        review.customer.id.eq(customerId)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return reviews.stream()
                .map(ReviewResponse::from)
                .toList();
    }

    @Override
    public long countByCustomerId(final Long customerId) {
        Long count = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.customer.id.eq(customerId)
                )
                .fetchOne();
        return (count == null ? 0L : count);
    }

}
