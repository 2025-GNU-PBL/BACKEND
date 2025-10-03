package gnu.project.backend.product.entity;

import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.enurmerated.Category;
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
@Table(name = "makeup")
@DiscriminatorValue("MAKEUP")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Makeup extends Product {


    @Column
    private String style;

    @Column
    private String availableTimes;

    @Column
    private String type;

    private Makeup(Owner owner, Integer price, String address, String detail,
        String name, String style, String availableTimes, String type) {
        super(owner, Category.MAKEUP, price, address, detail, name);
        this.style = style;
        this.availableTimes = availableTimes;
        this.type = type;
    }

    public static Makeup create(Owner owner, Integer price, String address,
        String detail, String name, String style, String availableTimes, String type) {
        return new Makeup(owner, price, address, detail, name, style, availableTimes, type);
    }

    public void update(
        final Integer price,
        final String address,
        final String detail,
        final String name,
        final String style,
        final String availableTimes,
        final String type
    ) {
        super.updateProduct(price, address, detail, name);
        this.style = style;
        this.availableTimes = availableTimes;
        this.type = type;
    }
}
