package gnu.project.backend.coupon.repository;

import gnu.project.backend.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>,
    UserCouponCustomRepository {
    

}
