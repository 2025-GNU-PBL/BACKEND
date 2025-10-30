package gnu.project.backend.coupon.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.coupon.dto.request.CouponRequestDto;
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

    @Column(name = "max_usage_per_user")
    private Integer maxUsagePerUser = 1;

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
        Integer maxUsagePerUser
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
        this.maxUsagePerUser = (maxUsagePerUser != null) ? maxUsagePerUser : 1;
    }

    public static Coupon createCoupon(
        final CouponRequestDto dto,
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
            dto.maxUsagePerUser()
        );
    }

    public void deactivate() {
        super.delete();
    }

    public boolean isValidOwner(final String socialId) {
        return this.getOwner().getSocialId().equals(socialId);
    }
}
