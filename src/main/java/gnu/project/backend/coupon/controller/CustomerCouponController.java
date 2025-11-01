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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/coupon")
public class CustomerCouponController {

    private final CustomerCouponService customerCouponService;


    @GetMapping("/{couponId}")
    public ResponseEntity<CustomerCouponResponseDto> getCouponDetail(
        @PathVariable final Long couponId
    ) {
        return ResponseEntity.ok(
            customerCouponService.getCouponDetail(couponId)
        );
    }

    @PostMapping("/{couponId}/download")
    public ResponseEntity<CustomerCouponResponseDto> downloadCoupon(
        @PathVariable final Long couponId,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.downloadCoupon(couponId, accessor)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<CustomerCouponResponseDto>> getMyCoupons(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.getMyCoupons(accessor)
        );
    }

    @GetMapping("/my/available")
    public ResponseEntity<List<CustomerCouponResponseDto>> getMyAvailableCoupons(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.getMyAvailableCoupons(accessor)
        );
    }

    @PostMapping("/my/{userCouponId}/use")
    public ResponseEntity<CustomerCouponResponseDto> useCoupon(
        @PathVariable final Long userCouponId,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            customerCouponService.useCoupon(userCouponId, accessor)
        );
    }

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