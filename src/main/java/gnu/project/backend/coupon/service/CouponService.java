package gnu.project.backend.coupon.service;

import static gnu.project.backend.common.error.ErrorCode.COUPON_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_SOCIAL;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.PRODUCT_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.coupon.dto.request.CouponRequestDto;
import gnu.project.backend.coupon.dto.response.CouponResponseDto;
import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.repository.CouponRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final OwnerRepository ownerRepository;

    public CouponResponseDto issueCoupon(final CouponRequestDto request, final Accessor accessor) {
        final Product product = productRepository.findByIdWithOwner(request.productId())
            .orElseThrow(() -> new BusinessException(
                PRODUCT_NOT_FOUND_EXCEPTION)
            );
        if (!product.validOwner(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }
        final Coupon coupon = Coupon.createCoupon(request, product.getOwner(), product);
        final Coupon savedCoupon = couponRepository.save(coupon);
        return CouponResponseDto.toResponse(savedCoupon);
    }

    public CouponResponseDto deleteCoupon(final Long couponId, final Accessor accessor) {
        final Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new BusinessException(COUPON_NOT_FOUND_EXCEPTION));

        if (!coupon.isValidOwner(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }
        coupon.deactivate();
        final Coupon savedCoupon = couponRepository.save(coupon);
        return CouponResponseDto.toResponse(savedCoupon);
    }

    public CouponResponseDto getCoupon(final Long couponId, final Accessor accessor) {
        final Coupon coupon = couponRepository.findCouponWithOwner(couponId).orElseThrow(
            () -> new BusinessException(COUPON_NOT_FOUND_EXCEPTION)
        );

        if (coupon.isValidOwner(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }

        return CouponResponseDto.toResponse(coupon);
    }

    public List<CouponResponseDto> getMyCoupons(final Accessor accessor) {
        final Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));

        final List<Coupon> coupons = couponRepository.findCoupons(owner.getId());
        
        return coupons.stream()
            .map(CouponResponseDto::toResponse)
            .collect(Collectors.toList());
    }

}
