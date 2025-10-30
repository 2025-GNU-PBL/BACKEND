package gnu.project.backend.coupon.event;

import gnu.project.backend.coupon.entity.Coupon;
import java.time.LocalDate;

public record CouponExpiredEvent(Long couponId, LocalDate expiredAt) {

    public static CouponExpiredEvent from(Coupon coupon) {
        return new CouponExpiredEvent(coupon.getId(), LocalDate.now());
    }
}
