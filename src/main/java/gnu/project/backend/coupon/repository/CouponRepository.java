package gnu.project.backend.coupon.repository;

import gnu.project.backend.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponCustomRepository {

}
