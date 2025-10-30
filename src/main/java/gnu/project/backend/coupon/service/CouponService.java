package gnu.project.backend.coupon.service;

import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_SOCIAL;
import static gnu.project.backend.common.error.ErrorCode.PRODUCT_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.coupon.dto.request.CouponRequestDto;
import gnu.project.backend.coupon.dto.response.CouponResponseDto;
import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.repository.CouponRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;

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
}
