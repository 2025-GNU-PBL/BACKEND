package gnu.project.backend.product.entity;


import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.enurmerated.Category;
import gnu.project.backend.product.enurmerated.Region;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wedding_hall")
@DiscriminatorValue("WEDDING_HALL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WeddingHall extends Product{

    @Column
    private Integer capacity;

    @Column(name = "min_guest")
    private Integer minGuest;

    @Column(name = "max_guest")
    private Integer maxGuest;

    @Column(name = "hall_type")
    private Integer hallType;

    @Column(name = "parking_capacity")
    private Integer parkingCapacity;

    @Column(name = "catering_type", length = 100)
    private String cateringType;

    @Column(name = "available_times")
    private String availableTimes;

    @Lob
    @Column(name = "reservation_policy")
    private String reservationPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false, length = 20)
    private Region region;

    private WeddingHall(
            Owner owner,
            Integer price,
            String address,
            String detail,
            String name,
            Integer capacity,
            Integer minGuest,
            Integer maxGuest,
            Integer hallType,
            Integer parkingCapacity,
            String cateringType,
            String availableTimes,
            String reservationPolicy,
            Region region
    ) {
        super(
                owner,
                Category.WEDDING_HALL,
                price,
                address,
                detail,
                name
        );
        this.capacity = capacity;
        this.minGuest = minGuest;
        this.maxGuest = maxGuest;
        this.hallType = hallType;
        this.parkingCapacity = parkingCapacity;
        this.cateringType = cateringType;
        this.availableTimes = availableTimes;
        this.reservationPolicy = reservationPolicy;
        this.region = region;
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
            Integer hallType,
            Integer parkingCapacity,
            String cateringType,
            String availableTimes,
            String reservationPolicy,
            Region region
    ) {
        return new WeddingHall(
                owner,
                price,
                address,
                detail,
                name,
                capacity,
                minGuest,
                maxGuest,
                hallType,
                parkingCapacity,
                cateringType,
                availableTimes,
                reservationPolicy,
                region
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
            Integer hallType,
            Integer parkingCapacity,
            String cateringType,
            String availableTimes,
            String reservationPolicy,
            Region region
    ) {
        super.updateProduct(price, address, detail, name);
        this.capacity = capacity;
        this.minGuest = minGuest;
        this.maxGuest = maxGuest;
        this.hallType = hallType;
        this.parkingCapacity = parkingCapacity;
        this.cateringType = cateringType;
        this.availableTimes = availableTimes;
        this.reservationPolicy = reservationPolicy;
        this.region = region;
    }


}
