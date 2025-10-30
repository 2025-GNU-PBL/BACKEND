package gnu.project.backend.coupon.repository;

public interface UserCouponCustomRepository {

    boolean existsByCoupon(final Long couponId);
}
