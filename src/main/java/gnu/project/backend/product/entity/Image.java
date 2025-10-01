package gnu.project.backend.product.entity;

import gnu.project.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    private Image(
        final Product product,
        final String url,
        final String s3Key,
        final Integer displayOrder
    ) {
        this.product = product;
        this.url = url;
        this.s3Key = s3Key;
        this.displayOrder = displayOrder;
    }

    public static Image ofCreate(
        final Product product,
        final String url,
        final String s3Key,
        final Integer displayOrder
    ) {
        return new Image(product, url, s3Key, displayOrder);
    }

    public void updateDisplayOrder(Integer newOrder) {
        this.displayOrder = newOrder;
    }

    public boolean isThumbnail() {
        return this.displayOrder == 0;
    }

}
