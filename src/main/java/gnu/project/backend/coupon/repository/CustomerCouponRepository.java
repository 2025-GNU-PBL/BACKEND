package gnu.project.backend.coupon.repository;

import gnu.project.backend.coupon.entity.CustomerCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerCouponRepository extends JpaRepository<CustomerCoupon, Long>,
    CustomerCouponCustomRepository {


}
