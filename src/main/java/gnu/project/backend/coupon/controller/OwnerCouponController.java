package gnu.project.backend.coupon.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.coupon.dto.request.OwnerCouponRequestDto;
import gnu.project.backend.coupon.dto.response.OwnerCouponResponseDto;
import gnu.project.backend.coupon.service.OwnerCouponService;
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
@RequestMapping("api/v1/owner/coupon")
public class OwnerCouponController {

    private final OwnerCouponService ownerCouponService;

    @PostMapping()
    public ResponseEntity<OwnerCouponResponseDto> issueCoupon(
        @RequestBody final OwnerCouponRequestDto request,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(ownerCouponService.issueCoupon(request, accessor));
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<OwnerCouponResponseDto> deleteCoupon(
        @PathVariable(name = "couponId") final Long couponId,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(ownerCouponService.deleteCoupon(couponId, accessor));
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<OwnerCouponResponseDto> getCoupon(
        @PathVariable(name = "couponId") final Long couponId,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            ownerCouponService.getCoupon(couponId, accessor)
        );
    }

    @PatchMapping("/{couponId}")
    public ResponseEntity<OwnerCouponResponseDto> updateCoupon(
        @PathVariable(name = "couponId") final Long couponId,
        @RequestBody final OwnerCouponRequestDto request,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            ownerCouponService.updateCoupon(couponId, request, accessor)
        );
    }


    @GetMapping()
    public ResponseEntity<List<OwnerCouponResponseDto>> getCoupon(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(
            ownerCouponService.getMyCoupons(accessor)
        );
    }
}
