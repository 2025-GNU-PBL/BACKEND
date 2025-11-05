package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.WeddingHallTag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WeddingHallCustomRepository {

    WeddingHallResponse findByWeddingHallId(final Long id);


    Page<WeddingHallPageResponse> searchWeddingHallByOwner(
        final String ownerSocialId,
        final Pageable pageable
    );

    Optional<WeddingHall> findWeddingHallWithImagesAndOptionsById(final Long id);

    long countActive();

    long countActiveByOwner(final String ownerSocialId);

    List<WeddingHallPageResponse> searchWeddingHallByFilter(
        List<WeddingHallTag> tags, Category category, Region region,
        Integer minPrice, Integer maxPrice, SortType sortType,
        Integer pageNumber, Integer pageSize
    );

    Long countWeddingHallByFilter(
        List<WeddingHallTag> tags,
        Category category,
        Region region,
        Integer minPrice,
        Integer maxPrice
    );

}
