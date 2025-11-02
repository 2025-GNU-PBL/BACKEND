package gnu.project.backend.coupon.dto.request;

import gnu.project.backend.coupon.enumerated.DiscountType;
import gnu.project.backend.product.enumerated.Category;
import java.math.BigDecimal;
import java.time.LocalDate;

public record OwnerCouponRequestDto(
    Long productId,
    String couponCode,               // 쿠폰 코드
    String couponName,               // 쿠폰 이름
    String couponDetail,             // 쿠폰 상세 설명
    DiscountType discountType,       // 할인 유형 (정액/정율)
    BigDecimal discountValue,        // 할인 값 (예: 1000원 또는 10%)
    BigDecimal maxDiscountAmount,    // 최대 할인 금액 (정율 할인 시 필요)
    BigDecimal minPurchaseAmount,    // 최소 구매 금액
    Category category,               // 카테고리 기준 쿠폰인 경우
    LocalDate startDate,             // 쿠폰 시작일
    LocalDate expirationDate        // 만료일
) {

}
