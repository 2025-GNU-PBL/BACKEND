package gnu.project.backend.product.entity;

import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.enumerated.Category;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "studio")
@DiscriminatorValue("STUDIO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Studio extends Product {


    @Column
    private String availableTimes;

    private Studio(Owner owner, Integer price, String address, String detail,
        String name, String availableTimes) {
        super(owner, Category.DRESS, price, address, detail, name);
        this.availableTimes = availableTimes;
    }

    public static Studio create(Owner owner, Integer price, String address,
        String detail, String name, String availableTimes) {
        return new Studio(owner, price, address, detail, name, availableTimes);
    }

    public void update(
        final Integer price,
        final String address,
        final String detail,
        final String name,
        final String availableTimes
    ) {
        super.updateProduct(price, address, detail, name);
        this.availableTimes = availableTimes;
    }

}
