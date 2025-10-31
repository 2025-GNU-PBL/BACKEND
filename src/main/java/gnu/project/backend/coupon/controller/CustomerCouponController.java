package gnu.project.backend.coupon.controller;


import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.coupon.dto.response.CustomerCouponResponseDto;
import gnu.project.backend.coupon.service.CustomerCouponService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 손님(Customer)용 쿠폰 API - 쿠폰 검색, 다운로드, 사용 - 내 쿠폰 조회
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/coupons")
public class CustomerCouponController {

    private final CustomerCouponService customerCouponService;


    /**
     * 쿠폰 상세 조회 (다운로드 전 미리보기)
     */
    @GetMapping("/{couponId}")
    public ResponseEntity<CustomerCouponResponseDto> getCouponDetail(
        @PathVariable final Long couponId
    ) {
        return ResponseEntity.ok(
            customerCouponService.getCouponDetail(couponId)
        );
    }

    /**
     * 쿠폰 다운로드
     */
    @PostMapping("/{couponId}/download")
    public ResponseEntity<CustomerCouponResponseDto> downloadCoupon(
        @PathVariable final Long couponId,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.downloadCoupon(couponId, accessor)
        );
    }

    /**
     * 내가 다운로드한 쿠폰 목록
     */
    @GetMapping("/my")
    public ResponseEntity<List<CustomerCouponResponseDto>> getMyCoupons(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.getMyCoupons(accessor)
        );
    }

    /**
     * 사용 가능한 내 쿠폰 목록
     */
    @GetMapping("/my/available")
    public ResponseEntity<List<CustomerCouponResponseDto>> getMyAvailableCoupons(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.getMyAvailableCoupons(accessor)
        );
    }

    /**
     * 쿠폰 사용
     */
    @PostMapping("/my/{userCouponId}/use")
    public ResponseEntity<CustomerCouponResponseDto> useCoupon(
        @PathVariable final Long userCouponId,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.useCoupon(userCouponId, accessor)
        );
    }

    /**
     * 특정 상품에 사용 가능한 내 쿠폰 조회
     */
    @GetMapping("/my/applicable")
    public ResponseEntity<List<CustomerCouponResponseDto>> getApplicableCoupons(
        @RequestParam final Long productId,
        @RequestParam final Long purchaseAmount,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.getApplicableCoupons(productId, purchaseAmount, accessor)
        );
    }
}