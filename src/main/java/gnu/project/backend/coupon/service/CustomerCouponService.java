package gnu.project.backend.coupon.service;

import static gnu.project.backend.common.error.ErrorCode.COUPON_ALREADY_DOWNLOADED;
import static gnu.project.backend.common.error.ErrorCode.COUPON_ALREADY_USED;
import static gnu.project.backend.common.error.ErrorCode.COUPON_CANCELLED;
import static gnu.project.backend.common.error.ErrorCode.COUPON_EXPIRED;
import static gnu.project.backend.common.error.ErrorCode.COUPON_NOT_AVAILABLE;
import static gnu.project.backend.common.error.ErrorCode.COUPON_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.COUPON_NOT_STARTED;
import static gnu.project.backend.common.error.ErrorCode.COUPON_OLD_VERSION;
import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_COUPON_NOT_FOUND;
import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_CUSTOMER;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.coupon.dto.response.CustomerCouponResponseDto;
import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.entity.CustomerCoupon;
import gnu.project.backend.coupon.enumerated.CouponStatus;
import gnu.project.backend.coupon.enumerated.UserCouponStatus;
import gnu.project.backend.coupon.repository.CouponRepository;
import gnu.project.backend.coupon.repository.CustomerCouponRepository;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerCouponService {

    private final CouponRepository couponRepository;
    private final CustomerCouponRepository customerCouponRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerCouponResponseDto getCouponDetail(final Long couponId) {
        final Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new BusinessException(COUPON_NOT_FOUND_EXCEPTION)
            );

        if (!coupon.isUsable()) {
            throw new BusinessException(COUPON_NOT_AVAILABLE);
        }

        return CustomerCouponResponseDto.fromCoupon(coupon);
    }

    public CustomerCouponResponseDto downloadCoupon(final Long couponId, final Accessor accessor) {
        final Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new BusinessException(COUPON_NOT_FOUND_EXCEPTION));

        final Customer customer = customerRepository.findByOauthInfo_SocialId(
                accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION)
            );

        validateCouponForDownload(coupon);

        if (customerCouponRepository.existsByCouponAndCustomer(coupon.getId(), customer.getId())) {
            throw new BusinessException(COUPON_ALREADY_DOWNLOADED);
        }

        final CustomerCoupon customerCoupon = CustomerCoupon.ofCreate(
            customer,
            coupon,
            UserCouponStatus.AVAILABLE
        );
        final CustomerCoupon savedCoupon = customerCouponRepository.save(customerCoupon);

        coupon.increaseDownloadCount();
        couponRepository.save(coupon);

        return CustomerCouponResponseDto.from(savedCoupon);
    }

    @Transactional(readOnly = true)
    public List<CustomerCouponResponseDto> getMyCoupons(final Accessor accessor) {
        final Customer customer = customerRepository.findByOauthInfo_SocialId(
                accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));

        final List<CustomerCoupon> customerCoupons = customerCouponRepository
            .findByCustomerWithCoupon(customer);

        return customerCoupons.stream()
            .map(CustomerCouponResponseDto::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CustomerCouponResponseDto> getMyAvailableCoupons(final Accessor accessor) {
        final Customer customer = customerRepository.findByOauthInfo_SocialId(
                accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));

        final List<CustomerCoupon> availableCoupons = customerCouponRepository
            .findAvailableCouponsByCustomer(customer);

        return availableCoupons.stream()
            .map(CustomerCouponResponseDto::from)
            .collect(Collectors.toList());
    }

    public CustomerCouponResponseDto useCoupon(final Long userCouponId, final Accessor accessor) {
        final Customer customer = customerRepository.findByOauthInfo_SocialId(
                accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));

        final CustomerCoupon customerCoupon = customerCouponRepository
            .findByIdWithCoupon(userCouponId)
            .orElseThrow(() -> new BusinessException(CUSTOMER_COUPON_NOT_FOUND));

        if (!customerCoupon.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException(IS_NOT_VALID_CUSTOMER);
        }

        validateCouponForUse(customerCoupon);

        customerCoupon.markAsUsed();
        customerCoupon.getCoupon().increaseUsageCount();

        customerCouponRepository.save(customerCoupon);
        couponRepository.save(customerCoupon.getCoupon());

        return CustomerCouponResponseDto.from(customerCoupon);
    }

    @Transactional(readOnly = true)
    public List<CustomerCouponResponseDto> getApplicableCoupons(
        final Long productId,
        final Long purchaseAmount,
        final Accessor accessor
    ) {
        final Customer customer = customerRepository.findByOauthInfo_SocialId(
                accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));

        final List<CustomerCoupon> availableCoupons = customerCouponRepository
            .findAvailableCouponsByCustomer(customer);

        return availableCoupons.stream()
            .filter(cc -> isApplicableToProduct(cc, productId, BigDecimal.valueOf(purchaseAmount)))
            .map(CustomerCouponResponseDto::from)
            .collect(Collectors.toList());
    }


    private void validateCouponForDownload(final Coupon coupon) {
        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new BusinessException(COUPON_NOT_AVAILABLE);
        }

        if (!coupon.getIsLatestVersion()) {
            throw new BusinessException(COUPON_OLD_VERSION);
        }

        if (coupon.isExpired()) {
            throw new BusinessException(COUPON_EXPIRED);
        }

        if (LocalDate.now().isBefore(coupon.getStartDate())) {
            throw new BusinessException(COUPON_NOT_STARTED);
        }
    }

    private void validateCouponForUse(final CustomerCoupon customerCoupon) {
        if (customerCoupon.getStatus() == UserCouponStatus.USED) {
            throw new BusinessException(COUPON_ALREADY_USED);
        }

        if (customerCoupon.getStatus() == UserCouponStatus.CANCELLED) {
            throw new BusinessException(COUPON_CANCELLED);
        }

        if (customerCoupon.getStatus() == UserCouponStatus.EXPIRED) {
            throw new BusinessException(COUPON_EXPIRED);
        }

        final Coupon coupon = customerCoupon.getCoupon();
        if (coupon.isExpired()) {
            throw new BusinessException(COUPON_EXPIRED);
        }

        if (!coupon.isUsable()) {
            throw new BusinessException(COUPON_NOT_AVAILABLE);
        }
    }

    private boolean isApplicableToProduct(
        final CustomerCoupon customerCoupon,
        final Long productId,
        final BigDecimal purchaseAmount
    ) {
        final Coupon coupon = customerCoupon.getCoupon();

        if (coupon.getProduct() != null && !coupon.getProduct().getId().equals(productId)) {
            return false;
        }

        return coupon.getMinPurchaseAmount() == null ||
            purchaseAmount.compareTo(coupon.getMinPurchaseAmount()) >= 0;
    }
}