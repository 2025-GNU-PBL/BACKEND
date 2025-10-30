package gnu.project.backend.coupon.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.coupon.dto.request.CouponRequestDto;
import gnu.project.backend.coupon.dto.response.CouponResponseDto;
import gnu.project.backend.coupon.service.CouponService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/coupon")
public class CouponController {

    private final CouponService couponService;

    @PostMapping()
    public ResponseEntity<CouponResponseDto> issueCoupon(
        @RequestBody final CouponRequestDto request,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(couponService.issueCoupon(request, accessor));
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<CouponResponseDto> deleteCoupon(
        @PathVariable(name = "couponId") final Long couponId,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(couponService.deleteCoupon(couponId, accessor));
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<CouponResponseDto> getCoupon(
        @PathVariable(name = "couponId") final Long couponId,
        final Accessor accessor
    ) {
        return ResponseEntity.ok(
            couponService.getCoupon(couponId, accessor)
        );
    }

    @PatchMapping("/{couponId}")
    public ResponseEntity<CouponResponseDto> updateCoupon(
        @PathVariable(name = "couponId") final Long couponId,
        @RequestBody final CouponRequestDto request,
        final Accessor accessor
    ) {
        return ResponseEntity.ok(
            couponService.updateCoupon(couponId, request, accessor)
        );
    }


    @GetMapping()
    public ResponseEntity<List<CouponResponseDto>> getCoupon(
        final Accessor accessor
    ) {
        return ResponseEntity.ok(
            couponService.getMyCoupons(accessor)
        );
    }
}
