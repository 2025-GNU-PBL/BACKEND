package gnu.project.backend.product.entity;

import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "dress")
@DiscriminatorValue("DRESS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dress extends Product {


    private Dress(Owner owner, Integer price, String address, String detail,
        String name, String availableTimes, Region region) {
        super(owner, Category.DRESS, price, address, detail, name, availableTimes, region);
    }

    public static Dress create(Owner owner, Integer price, String address,
        String detail, String name, String availableTimes, Region region) {
        return new Dress(owner, price, address, detail, name, availableTimes, region);
    }

    public void update(
        final Integer price,
        final String address,
        final String detail,
        final String name,
        final String availableTimes,
        final Region region
    ) {
        super.updateProduct(price, address, detail, name, availableTimes, region);
    }

}
