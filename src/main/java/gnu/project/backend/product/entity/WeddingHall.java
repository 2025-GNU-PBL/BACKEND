package gnu.project.backend.product.entity;

import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wedding_hall")
@DiscriminatorValue("WEDDING_HALL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WeddingHall extends Product {

    @Column
    private Integer capacity;

    @Column(name = "min_guest")
    private Integer minGuest;

    @Column(name = "max_guest")
    private Integer maxGuest;

    @Column(name = "parking_capacity")
    private Integer parkingCapacity;

    @Column(name = "catering_type", length = 100)
    private String cateringType;

    @Lob
    @Column(name = "reservation_policy")
    private String reservationPolicy;

    // ✅ 새 필터 컬럼
    @Column(name = "subway_accessible", nullable = false)
    private boolean subwayAccessible;

    @Column(name = "dining_available", nullable = false)
    private boolean diningAvailable;

    private WeddingHall(
            Owner owner,
            Integer price,
            String address,
            String detail,
            String name,
            Integer capacity,
            Integer minGuest,
            Integer maxGuest,
            Integer parkingCapacity,
            String cateringType,
            String availableTimes,
            String reservationPolicy,
            Region region,
            boolean subwayAccessible,
            boolean diningAvailable
    ) {
        super(owner, Category.WEDDING_HALL, price, address, detail, name, availableTimes, region);
        this.capacity = capacity;
        this.minGuest = minGuest;
        this.maxGuest = maxGuest;
        this.parkingCapacity = parkingCapacity;
        this.cateringType = cateringType;
        this.reservationPolicy = reservationPolicy;
        this.subwayAccessible = subwayAccessible;
        this.diningAvailable = diningAvailable;
    }

    public static WeddingHall create(
            Owner owner,
            Integer price,
            String address,
            String detail,
            String name,
            Integer capacity,
            Integer minGuest,
            Integer maxGuest,
            Integer parkingCapacity,
            String cateringType,
            String availableTimes,
            String reservationPolicy,
            Region region,
            boolean subwayAccessible,
            boolean diningAvailable
    ) {
        return new WeddingHall(
                owner, price, address, detail, name,
                capacity, minGuest, maxGuest, parkingCapacity,
                cateringType, availableTimes, reservationPolicy, region,
                subwayAccessible, diningAvailable
        );
    }

    public void update(
            Integer price,
            String address,
            String detail,
            String name,
            Integer capacity,
            Integer minGuest,
            Integer maxGuest,
            Integer parkingCapacity,
            String cateringType,
            String availableTimes,
            String reservationPolicy,
            Region region,
            boolean subwayAccessible,
            boolean diningAvailable
    ) {
        super.updateProduct(price, address, detail, name, availableTimes, region);
        this.capacity = capacity;
        this.minGuest = minGuest;
        this.maxGuest = maxGuest;
        this.parkingCapacity = parkingCapacity;
        this.cateringType = cateringType;
        this.reservationPolicy = reservationPolicy;
        this.subwayAccessible = subwayAccessible;
        this.diningAvailable = diningAvailable;
    }
}
