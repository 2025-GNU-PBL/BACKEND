package gnu.project.backend.coupon.dto.response;

import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.enumerated.CouponStatus;
import gnu.project.backend.coupon.enumerated.DiscountType;
import gnu.project.backend.product.enumerated.Category;
import java.math.BigDecimal;
import java.time.LocalDate;

public record OwnerCouponResponseDto(
    Long id,
    String couponCode,
    DiscountType discountType,
    BigDecimal discountValue,
    LocalDate startDate,
    LocalDate expirationDate,
    String couponName,
    String couponDetail,
    Category category,
    BigDecimal maxDiscountAmount,
    BigDecimal minPurchaseAmount,
    Integer currentUsageCount,
    Long ownerId,
    Long productId,
    CouponStatus status

) {

    public static OwnerCouponResponseDto toResponse(final Coupon coupon) {
        return new OwnerCouponResponseDto(
            coupon.getId(),
            coupon.getCouponCode(),
            coupon.getDiscountType(),
            coupon.getDiscountValue(),
            coupon.getStartDate(),
            coupon.getExpirationDate(),
            coupon.getCouponName(),
            coupon.getCouponDetail(),
            coupon.getCategory(),
            coupon.getMaxDiscountAmount(),
            coupon.getMinPurchaseAmount(),
            coupon.getCurrentUsageCount(),
            (coupon.getOwner() != null) ? coupon.getOwner().getId() : null,
            (coupon.getProduct() != null) ? coupon.getProduct().getId() : null,
            coupon.getStatus()
        );
    }
}
