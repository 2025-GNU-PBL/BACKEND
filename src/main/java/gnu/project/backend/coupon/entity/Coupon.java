package gnu.project.backend.coupon.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.coupon.dto.request.OwnerCouponRequestDto;
import gnu.project.backend.coupon.enumerated.CouponStatus;
import gnu.project.backend.coupon.enumerated.DiscountType;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.enurmerated.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @JoinColumn(name = "owner_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Owner owner;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount_type")
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "coupon_name")
    private String couponName;

    @Column(name = "coupon_detail")
    private String couponDetail;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_purchase_amount")
    private BigDecimal minPurchaseAmount;

    @Column(name = "current_usage_count")
    private Integer currentUsageCount = 0;


    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "parent_coupon_id")
    private Long parentCouponId;

    @Column(name = "is_latest_version", nullable = false)
    private Boolean isLatestVersion = true;

    @Column(name = "current_download_count", nullable = false)
    private Integer currentDownloadCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CouponStatus status = CouponStatus.ACTIVE;


    private Coupon(
        Product product,
        Owner owner,
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
        Integer version,
        Long parentCouponId
    ) {
        this.product = product;
        this.owner = owner;
        this.couponCode = couponCode;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
        this.couponName = couponName;
        this.couponDetail = couponDetail;
        this.category = category;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minPurchaseAmount = minPurchaseAmount;
        this.currentUsageCount = 0;
        this.version = (version != null) ? version : 1;
        this.parentCouponId = parentCouponId;
        this.isLatestVersion = true;
        this.currentDownloadCount = 0;
    }

    public static Coupon createCoupon(
        final OwnerCouponRequestDto dto,
        final Owner owner,
        final Product product
    ) {
        return new Coupon(
            product,
            owner,
            dto.couponCode(),
            dto.discountType(),
            dto.discountValue(),
            dto.startDate(),
            dto.expirationDate(),
            dto.couponName(),
            dto.couponDetail(),
            dto.category(),
            dto.maxDiscountAmount(),
            dto.minPurchaseAmount(),
            1,  // 첫 버전
            null  // 부모 없음
        );
    }

    public static Coupon createNewVersion(final Coupon oldCoupon,
        final OwnerCouponRequestDto updateDto) {
        {
            return new Coupon(
                oldCoupon.getProduct(),
                oldCoupon.getOwner(),
                updateDto.couponCode(),
                updateDto.discountType(),
                updateDto.discountValue(),
                updateDto.startDate(),
                updateDto.expirationDate(),
                updateDto.couponName(),
                updateDto.couponDetail(),
                updateDto.category(),
                updateDto.maxDiscountAmount(),
                updateDto.minPurchaseAmount(),
                oldCoupon.getVersion() + 1,
                oldCoupon.getId()
            );
        }
    }


    public boolean isValidOwner(final String socialId) {
        return this.getOwner().getSocialId().equals(socialId);
    }

    public void deactivate() {
        this.status = CouponStatus.INACTIVE;
        super.delete();
    }

    public void markAsOldVersion() {
        this.isLatestVersion = false;
    }


    public void update(final OwnerCouponRequestDto dto) {
        this.couponCode = dto.couponCode();
        this.couponName = dto.couponName();
        this.couponDetail = dto.couponDetail();
        this.discountType = dto.discountType();
        this.discountValue = dto.discountValue();
        this.maxDiscountAmount = dto.maxDiscountAmount();
        this.minPurchaseAmount = dto.minPurchaseAmount();
        this.category = dto.category();
        this.startDate = dto.startDate();
        this.expirationDate = dto.expirationDate();
    }

    public void increaseDownloadCount() {
        this.currentDownloadCount++;
    }

    public void increaseUsageCount() {
        this.currentUsageCount++;
    }

    public void expire() {
        this.status = CouponStatus.EXPIRED;
    }


    public boolean isUsable() {
        return this.status == CouponStatus.ACTIVE && !isExpired();
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expirationDate);
    }

}
