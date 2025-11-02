package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.entity.WeddingHall;
import gnu.project.backend.product.enumerated.Region;

import java.util.List;
import java.util.Optional;

public interface WeddingHallCustomRepository {

    WeddingHallResponse findByWeddingHallId(final Long id);

    List<WeddingHallPageResponse> searchWeddingHall(final int pageSize, final int pageNumber,
        final Region region);

    long countActiveByRegion(final Region region);

    List<WeddingHallPageResponse> searchWeddingHallByOwner(
        final String ownerSocialId,
        final int pageSize,
        final int pageNumber
    );

    Optional<WeddingHall> findWeddingHallWithImagesAndOptionsById(final Long id);

    long countActive();

    long countActiveByOwner(final String ownerSocialId);


}
