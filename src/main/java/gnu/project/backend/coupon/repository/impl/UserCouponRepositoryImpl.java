package gnu.project.backend.coupon.repository.impl;

import static gnu.project.backend.coupon.entity.QUserCoupon.userCoupon;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.coupon.enumerated.UserCouponStatus;
import gnu.project.backend.coupon.repository.UserCouponCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponCustomRepository {

    private final JPAQueryFactory query;


    @Override
    public void deactivateAllByCouponId(final Long couponId) {
        query.update(userCoupon)
            .set(userCoupon.status, UserCouponStatus.CANCELLED)
            .where(userCoupon.coupon.id.eq(couponId))
            .execute();
    }
}
