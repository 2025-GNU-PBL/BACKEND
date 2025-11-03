package gnu.project.backend.coupon.dto.response;

import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.entity.CustomerCoupon;
import gnu.project.backend.coupon.enumerated.CustomerCouponStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CustomerCouponResponseDto(
    // CustomerCoupon 정보
    Long userCouponId,
    CustomerCouponStatus status,
    LocalDateTime downloadedAt,
    LocalDateTime usedAt,

    // Coupon 정보
    Long couponId,
    String couponCode,
    String couponName,
    String couponDetail,
    String discountType,
    BigDecimal discountValue,
    BigDecimal maxDiscountAmount,
    BigDecimal minPurchaseAmount,
    LocalDate startDate,
    LocalDate expirationDate,
    String category,

    Boolean canUse,  // 현재 사용 가능 여부
    Integer daysUntilExpiration,  // 만료까지 남은 일수

    Long productId,
    String productName
) {

    public static CustomerCouponResponseDto from(CustomerCoupon customerCoupon) {
        final Coupon coupon = customerCoupon.getCoupon();
        final LocalDate now = LocalDate.now();

        // 사용 가능 여부 판단
        final boolean canUse = customerCoupon.getStatus() == CustomerCouponStatus.AVAILABLE
            && coupon.isUsable()
            && !now.isAfter(coupon.getExpirationDate());

        // 만료까지 남은 일수 계산
        final int daysUntilExpiration = now.isBefore(coupon.getExpirationDate())
            ? (int) java.time.temporal.ChronoUnit.DAYS.between(now, coupon.getExpirationDate())
            : 0;

        return CustomerCouponResponseDto.builder()
            .userCouponId(customerCoupon.getId())
            .status(customerCoupon.getStatus())
            .downloadedAt(customerCoupon.getDownloadedAt())
            .usedAt(customerCoupon.getUsedAt())
            .couponId(coupon.getId())
            .couponCode(coupon.getCouponCode())
            .couponName(coupon.getCouponName())
            .couponDetail(coupon.getCouponDetail())
            .discountType(coupon.getDiscountType().name())
            .discountValue(coupon.getDiscountValue())
            .maxDiscountAmount(coupon.getMaxDiscountAmount())
            .minPurchaseAmount(coupon.getMinPurchaseAmount())
            .startDate(coupon.getStartDate())
            .expirationDate(coupon.getExpirationDate())
            .category(coupon.getCategory() != null ? coupon.getCategory().name() : null)
            .canUse(canUse)
            .daysUntilExpiration(daysUntilExpiration)
            .productId(coupon.getProduct() != null ? coupon.getProduct().getId() : null)
            .productName(coupon.getProduct() != null ? coupon.getProduct().getName() : null)
            .build();
    }

    /**
     * Coupon 엔티티로부터 DTO 생성 (다운로드 전 미리보기용)
     */
    public static CustomerCouponResponseDto fromCoupon(Coupon coupon) {
        final LocalDate now = LocalDate.now();

        final boolean canUse = coupon.isUsable() && !now.isAfter(coupon.getExpirationDate());

        final int daysUntilExpiration = now.isBefore(coupon.getExpirationDate())
            ? (int) java.time.temporal.ChronoUnit.DAYS.between(now, coupon.getExpirationDate())
            : 0;

        return CustomerCouponResponseDto.builder()
            .userCouponId(null)
            .status(null)
            .downloadedAt(null)
            .usedAt(null)
            .couponId(coupon.getId())
            .couponCode(coupon.getCouponCode())
            .couponName(coupon.getCouponName())
            .couponDetail(coupon.getCouponDetail())
            .discountType(coupon.getDiscountType().name())
            .discountValue(coupon.getDiscountValue())
            .maxDiscountAmount(coupon.getMaxDiscountAmount())
            .minPurchaseAmount(coupon.getMinPurchaseAmount())
            .startDate(coupon.getStartDate())
            .expirationDate(coupon.getExpirationDate())
            .category(coupon.getCategory() != null ? coupon.getCategory().name() : null)
            .canUse(canUse)
            .daysUntilExpiration(daysUntilExpiration)
            .productId(coupon.getProduct() != null ? coupon.getProduct().getId() : null)
            .productName(coupon.getProduct() != null ? coupon.getProduct().getName() : null)
            .build();
    }
}