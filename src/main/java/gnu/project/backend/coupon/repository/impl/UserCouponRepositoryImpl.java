package gnu.project.backend.coupon.repository.impl;

import static gnu.project.backend.coupon.entity.QUserCoupon.userCoupon;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.coupon.repository.UserCouponCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponCustomRepository {

    private final JPAQueryFactory query;


    @Override
    public boolean existsByCoupon(final Long couponId) {
        return query.selectOne()
            .from(userCoupon)
            .where(
                userCoupon.coupon.id.eq(couponId)
                    .and(userCoupon.isUsed.isFalse())
            )
            .fetchFirst() != null;
    }
}
