package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WeddingHallCustomRepository {

    WeddingHallResponse findByWeddingHallId(final Long id);

    Page<WeddingHallPageResponse> searchWeddingHall(
            final Pageable pageable,
            final Region region,
            final Boolean subwayAccessible,
            final Boolean diningAvailable
    );

    long countActiveFiltered(
            final Region region,
            final Boolean subwayAccessible,
            final Boolean diningAvailable
    );

    Page<WeddingHallPageResponse> searchWeddingHallByOwner(
            final String ownerSocialId,
            final Pageable pageable
    );

    Optional<WeddingHall> findWeddingHallWithImagesAndOptionsById(final Long id);

    long countActive();

    long countActiveByOwner(final String ownerSocialId);
}
