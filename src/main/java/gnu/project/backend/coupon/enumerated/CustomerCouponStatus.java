package gnu.project.backend.coupon.enumerated;

public enum CustomerCouponStatus {
    AVAILABLE,  // 다운로드했지만 미사용
    USED,       // 이미 사용됨
    EXPIRED,    // 유효기간 만료됨
    CANCELLED   // 사용 취소됨

}
