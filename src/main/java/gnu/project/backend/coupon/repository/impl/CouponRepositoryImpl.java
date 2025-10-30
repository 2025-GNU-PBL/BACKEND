package gnu.project.backend.coupon.repository.impl;

import static gnu.project.backend.coupon.entity.QCoupon.coupon;
import static gnu.project.backend.owner.entity.QOwner.owner;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.repository.CouponCustomRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponCustomRepository {

    private static final OrderSpecifier<?>[] COUPON_DEFAULT_ORDER = {
        coupon.id.desc(),
        coupon.createdAt.desc()
    };
    private final JPAQueryFactory query;

    @Override
    public Optional<Coupon> findCouponWithOwner(final Long couponId) {
        return Optional.ofNullable(
            query.selectFrom(coupon)
                .leftJoin(coupon.owner, owner)
                .fetchJoin()
                .where(coupon.id.eq(couponId))
                .fetchFirst()
        );
    }

    @Override
    public List<Coupon> findCoupons(final Long ownerId) {
        return query.selectFrom(coupon)
            .leftJoin(coupon.owner, owner)
            .fetchJoin()
            .where(coupon.owner.id.eq(ownerId))
            .orderBy(COUPON_DEFAULT_ORDER)
            .fetch();
    }
}
