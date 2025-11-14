package gnu.project.backend.product.entity;

import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "makeup")
@DiscriminatorValue("MAKEUP")
@PrimaryKeyJoinColumn(name = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Makeup extends Product {


    private Makeup(Owner owner, Integer price, String address, String detail,
        String name, String availableTimes, Region region) {
        super(owner, Category.MAKEUP, price, address, detail, name, availableTimes, region);
    }

    public static Makeup create(Owner owner, Integer price, String address,
        String detail, String name, String availableTimes,
        Region region) {
        return new Makeup(owner, price, address, detail, name, availableTimes, region);
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

    public void delete() {
        super.delete();
    }
}
