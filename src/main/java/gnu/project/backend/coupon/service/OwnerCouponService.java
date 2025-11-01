package gnu.project.backend.coupon.service;

import static gnu.project.backend.common.error.ErrorCode.COUPON_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_SOCIAL;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.PRODUCT_NOT_FOUND_EXCEPTION;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.coupon.dto.request.OwnerCouponRequestDto;
import gnu.project.backend.coupon.dto.response.OwnerCouponResponseDto;
import gnu.project.backend.coupon.entity.Coupon;
import gnu.project.backend.coupon.repository.CouponRepository;
import gnu.project.backend.coupon.repository.CustomerCouponRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OwnerCouponService {

    // TODO : 쿠폰 카테고리별 리스트업, 페이지네이션, 유저 쿠폰 부분
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final OwnerRepository ownerRepository;
    private final CustomerCouponRepository customerCouponRepository;

    public OwnerCouponResponseDto issueCoupon(final OwnerCouponRequestDto request,
        final Accessor accessor) {
        final Product product = productRepository.findByIdWithOwner(request.productId())
            .orElseThrow(() -> new BusinessException(
                PRODUCT_NOT_FOUND_EXCEPTION)
            );
        if (!product.validOwner(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }
        final Coupon coupon = Coupon.createCoupon(request, product.getOwner(), product);
        final Coupon savedCoupon = couponRepository.save(coupon);
        return OwnerCouponResponseDto.toResponse(savedCoupon);
    }

    public OwnerCouponResponseDto deleteCoupon(final Long couponId, final Accessor accessor) {
        final Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new BusinessException(COUPON_NOT_FOUND_EXCEPTION));

        if (!coupon.isValidOwner(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }
        coupon.deactivate();
        customerCouponRepository.deactivateAllByCouponId(couponId);
        final Coupon savedCoupon = couponRepository.save(coupon);
        return OwnerCouponResponseDto.toResponse(savedCoupon);
    }

    @Transactional(readOnly = true)
    public OwnerCouponResponseDto getCoupon(final Long couponId, final Accessor accessor) {
        final Coupon coupon = couponRepository.findCouponWithOwner(couponId).orElseThrow(
            () -> new BusinessException(COUPON_NOT_FOUND_EXCEPTION)
        );

        if (!coupon.isValidOwner(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }

        return OwnerCouponResponseDto.toResponse(coupon);
    }

    @Transactional(readOnly = true)
    public List<OwnerCouponResponseDto> getMyCoupons(final Accessor accessor) {
        final Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
            .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));

        final List<Coupon> coupons = couponRepository.findCoupons(owner.getId());

        return coupons.stream()
            .map(OwnerCouponResponseDto::toResponse)
            .collect(Collectors.toList());
    }

    /*
    update 시 기존에 있던 쿠폰은 어떻게 관리 해야 할까?
    설계 목표

    기존 쿠폰은 즉시 비활성화 (사용 불가 상태로 전환)
    새로운 쿠폰은 새로운 버전으로 발급하여 교체
     */
    public OwnerCouponResponseDto updateCoupon(
        final Long couponId,
        final OwnerCouponRequestDto updateDto,
        final Accessor accessor
    ) {
        final Coupon oldCoupon = couponRepository.findCouponWithOwner(couponId)
            .orElseThrow(() -> new BusinessException(COUPON_NOT_FOUND_EXCEPTION)
            );

        if (!oldCoupon.isValidOwner(accessor.getSocialId())) {
            throw new BusinessException(IS_NOT_VALID_SOCIAL);
        }

        oldCoupon.deactivate();
        oldCoupon.markAsOldVersion();

        couponRepository.save(oldCoupon);
        customerCouponRepository.deactivateAllByCouponId(couponId);

        final Coupon newCoupon = Coupon.createNewVersion(oldCoupon, updateDto);
        final Coupon updatedCoupon = couponRepository.save(newCoupon);

        return OwnerCouponResponseDto.toResponse(updatedCoupon);
    }
}
