package gnu.project.backend.coupon.repository;

public interface UserCouponCustomRepository {


    void deactivateAllByCouponId(final Long couponId);
}
