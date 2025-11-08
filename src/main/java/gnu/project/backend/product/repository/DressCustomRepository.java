package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.DressResponse;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.entity.Dress;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.DressTag;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface DressCustomRepository {

    DressResponse findByDressId(final Long id);

    List<ProductPageResponse> searchDress(final int pageSize, final int pageNumber);

    Optional<Dress> findDressWithImagesAndOptionsById(final Long id);

    List<ProductPageResponse> searchDressByFilter(
        final List<DressTag> tags, final Category category,
        final Region region, final Integer minPrice, final Integer maxPrice,
        final SortType sortType, final Integer pageNumber, final Integer pageSize
    );

    Long countDressByFilter(final List<DressTag> tags, final Category category, final Region region,
        final Integer minPrice, final Integer maxPrice);
}
