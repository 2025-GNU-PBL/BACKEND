package gnu.project.backend.coupon.repository;

import gnu.project.backend.coupon.entity.Coupon;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponCustomRepository {

    Optional<Coupon> findCouponWithOwner(final Long couponId);

    List<Coupon> findCoupons(final Long ownerId);
}
