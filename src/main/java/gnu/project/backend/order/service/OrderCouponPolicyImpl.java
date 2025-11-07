package gnu.project.backend.order.service;

import static gnu.project.backend.common.error.ErrorCode.*;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.entity.CustomerCoupon;
import gnu.project.backend.coupon.enumerated.CouponStatus;
import gnu.project.backend.coupon.enumerated.CustomerCouponStatus;
import gnu.project.backend.coupon.enumerated.DiscountType;
import gnu.project.backend.coupon.repository.CouponRepository;
import gnu.project.backend.coupon.repository.CustomerCouponRepository;
import gnu.project.backend.reservation.entity.Reservation;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderCouponPolicyImpl implements OrderCouponPolicy {

    private final CustomerCouponRepository customerCouponRepository;
    private final CouponRepository couponRepository;

    @Override
    public long previewDiscount(Reservation reservation,
                                long originalPrice,
                                Long customerCouponId,
                                String customerSocialId) {
        if (customerCouponId == null) return 0L;

        CustomerCoupon cc = customerCouponRepository.findByIdWithCoupon(customerCouponId)
                .orElseThrow(() -> new BusinessException(CUSTOMER_COUPON_NOT_FOUND));

        if (!cc.getCustomer().getOauthInfo().getSocialId().equals(customerSocialId)) {
            throw new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION);
        }

        validateUsable(cc);

        Coupon coupon = cc.getCoupon();

        if (coupon.getProduct() != null
                && !coupon.getProduct().getId().equals(reservation.getProduct().getId())) {
            throw new BusinessException(COUPON_NOT_AVAILABLE);
        }

        if (coupon.getMinPurchaseAmount() != null) {
            BigDecimal min = coupon.getMinPurchaseAmount();
            if (BigDecimal.valueOf(originalPrice).compareTo(min) < 0) {
                throw new BusinessException(COUPON_NOT_AVAILABLE);
            }
        }

        long discount = calcDiscount(originalPrice, coupon);
        return Math.max(0L, Math.min(discount, originalPrice));
    }

    @Override
    @Transactional
    public void markUsed(Long customerCouponId, String customerSocialId) {
        if (customerCouponId == null) return;
        CustomerCoupon cc = customerCouponRepository.findByIdWithCoupon(customerCouponId)
                .orElseThrow(() -> new BusinessException(CUSTOMER_COUPON_NOT_FOUND));

        if (!cc.getCustomer().getOauthInfo().getSocialId().equals(customerSocialId)) {
            throw new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION);
        }

        validateUsable(cc);

        cc.markAsUsed();
        cc.getCoupon().increaseUsageCount();

        customerCouponRepository.save(cc);
        couponRepository.save(cc.getCoupon());
    }

    private void validateUsable(CustomerCoupon cc) {
        if (cc.getStatus() == CustomerCouponStatus.USED) throw new BusinessException(COUPON_ALREADY_USED);
        if (cc.getStatus() == CustomerCouponStatus.CANCELLED) throw new BusinessException(COUPON_CANCELLED);
        if (cc.getStatus() == CustomerCouponStatus.EXPIRED) throw new BusinessException(COUPON_EXPIRED);

        Coupon coupon = cc.getCoupon();
        LocalDate now = LocalDate.now();

        if (coupon.getStatus() != CouponStatus.ACTIVE) throw new BusinessException(COUPON_NOT_AVAILABLE);
        if (!Boolean.TRUE.equals(coupon.getIsLatestVersion())) throw new BusinessException(COUPON_OLD_VERSION);
        if (coupon.getExpirationDate() != null && now.isAfter(coupon.getExpirationDate())) {
            throw new BusinessException(COUPON_EXPIRED);
        }
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new BusinessException(COUPON_NOT_STARTED);
        }
    }

    private long calcDiscount(long originalPrice, Coupon coupon) {
        if (coupon.getDiscountType() == DiscountType.AMOUNT) {
            BigDecimal v = coupon.getDiscountValue() != null ? coupon.getDiscountValue() : BigDecimal.ZERO;
            return v.longValue();
        }
        BigDecimal rate = coupon.getDiscountValue() != null ? coupon.getDiscountValue() : BigDecimal.ZERO;
        long raw = Math.round(originalPrice * rate.doubleValue() / 100.0);
        if (coupon.getMaxDiscountAmount() != null) {
            raw = Math.min(raw, coupon.getMaxDiscountAmount().longValue());
        }
        return raw;
    }
}
