package gnu.project.backend.product.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.product.enurmerated.Category;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Table
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Owner owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String detail;

    @Column
    private double starCount = 0;

    @Column
    private Integer averageRating = 0;

    @Column(nullable = false)
    private String name;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    protected Product(Owner owner, Category category, Integer price,
        String address, String detail, String name) {
        this.owner = owner;
        this.category = category;
        this.price = price;
        this.address = address;
        this.detail = detail;
        this.name = name;
    }

    protected void updateProduct(Integer price, String address, String detail, String name) {
        this.price = price;
        this.address = address;
        this.detail = detail;
        this.name = name;
    }

    public void addImage(final Image image) {
        this.images.add(image);
    }

    public void removeImage(final Image image) {
        this.images.remove(image);
        reorderImages();
    }

    public void addTag(final Tag tag) {
        this.tags.add(tag);
    }

    public void addAllTag(final List<Tag> tags) {
        this.tags.addAll(tags);
    }


    public void addOption(final Option option) {
        this.options.add(option);
    }

    public void addAllOption(final List<Option> options) {
        this.options.addAll(options);
    }


    private void reorderImages() {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).updateDisplayOrder(i);
        }
    }

    public Image getThumbnailImage() {
        return images.stream()
            .filter(Image::isThumbnail)
            .findFirst()
            .orElse(null);
    }

    public String getThumbnailUrl() {
        Image thumbnail = getThumbnailImage();
        return thumbnail != null ? thumbnail.getUrl() : null;
    }

    public List<Image> getNonThumbnailImages() {
        return images.stream()
            .filter(image -> !image.isThumbnail())
            .sorted(Comparator.comparing(Image::getDisplayOrder))
            .collect(Collectors.toList());
    }

    public void removeTag(final Tag tag) {
        this.tags.remove(tag);
    }
}
