package gnu.project.backend.coupon.repository.impl;


import static gnu.project.backend.coupon.entity.QCoupon.coupon;
import static gnu.project.backend.coupon.entity.QCustomerCoupon.customerCoupon;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.coupon.entity.CustomerCoupon;
import gnu.project.backend.coupon.enumerated.CouponStatus;
import gnu.project.backend.coupon.enumerated.UserCouponStatus;
import gnu.project.backend.coupon.repository.CustomerCouponCustomRepository;
import gnu.project.backend.customer.entity.Customer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerCouponRepositoryImpl implements CustomerCouponCustomRepository {

    private final JPAQueryFactory query;


    @Override
    public void deactivateAllByCouponId(final Long couponId) {
        query.update(customerCoupon)
            .set(customerCoupon.status, UserCouponStatus.CANCELLED)
            .where(customerCoupon.coupon.id.eq(couponId))
            .execute();
    }

    @Override
    public boolean existsByCouponAndCustomer(final Long couponId, final Long customerId) {
        Integer result = query
            .selectOne()
            .from(customerCoupon)
            .where(
                customerCoupon.coupon.id.eq(couponId),
                customerCoupon.customer.id.eq(customerId),
                customerCoupon.isDeleted.eq(false)
            )
            .fetchFirst();

        return result != null;
    }

    @Override
    public List<CustomerCoupon> findByCustomerWithCoupon(final Customer customer) {
        return query
            .selectFrom(customerCoupon)
            .join(customerCoupon.coupon, coupon).fetchJoin()
            .where(
                customerCoupon.customer.eq(customer),
                customerCoupon.isDeleted.eq(false)
            )
            .orderBy(customerCoupon.createdAt.desc())
            .fetch();
    }

    @Override
    public List<CustomerCoupon> findAvailableCouponsByCustomer(final Customer customer) {
        LocalDate now = LocalDate.now();
        return query
            .selectFrom(customerCoupon)
            .join(customerCoupon.coupon, coupon).fetchJoin()
            .where(
                customerCoupon.customer.eq(customer),
                customerCoupon.status.eq(UserCouponStatus.AVAILABLE),
                customerCoupon.isDeleted.eq(false),
                coupon.startDate.loe(now),
                coupon.expirationDate.goe(now),
                coupon.status.eq(CouponStatus.ACTIVE)
            )
            .orderBy(coupon.expirationDate.asc())
            .fetch();
    }

    @Override
    public Optional<CustomerCoupon> findByIdWithCoupon(final Long id) {
        CustomerCoupon result = query
            .selectFrom(customerCoupon)
            .join(customerCoupon.coupon, coupon).fetchJoin()
            .where(
                customerCoupon.id.eq(id),
                customerCoupon.isDeleted.eq(false)
            )
            .fetchOne();

        return Optional.ofNullable(result);
    }


    // 통계용 DTO
    record CustomerCouponStatistics(
        long totalCount,
        long availableCount,
        long usedCount,
        long expiredCount
    ) {

    }
}
