package gnu.project.backend.coupon.repository;

import gnu.project.backend.coupon.entity.CustomerCoupon;
import gnu.project.backend.customer.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerCouponCustomRepository {


    void deactivateAllByCouponId(final Long couponId);

    boolean existsByCouponAndCustomer(final Long couponId, final Long customerId);

    List<CustomerCoupon> findByCustomerWithCoupon(final Customer customer);

    List<CustomerCoupon> findAvailableCouponsByCustomer(Customer customer);

    Optional<CustomerCoupon> findByIdWithCoupon(final Long userCouponId);
}
