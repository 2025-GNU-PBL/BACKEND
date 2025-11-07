package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.StudioPageResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import gnu.project.backend.product.entity.Studio;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.enumerated.StudioTag;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface StudioCustomRepository {

    StudioResponse findByStudioId(final Long id);

    List<StudioPageResponse> searchStudio(final int pageSize, final int pageNumber);

    Optional<Studio> findStudioWithImagesAndOptionsById(final Long id);

    List<StudioPageResponse> searchStudiosByFilter(List<StudioTag> tags, Category category,
        Region region, Integer minPrice, Integer maxPrice, SortType sortType, Integer pageNumber,
        Integer pageSize);

    Long countStudiosByFilter(List<StudioTag> tags, Category category, Region region,
        Integer minPrice, Integer maxPrice);
}
