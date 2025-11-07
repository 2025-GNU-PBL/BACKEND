package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.MakeupPageResponse;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.MakeupTag;
import gnu.project.backend.product.enumerated.Region;
import gnu.project.backend.product.enumerated.SortType;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface MakeupCustomRepository {


    MakeupResponse findByMakeupId(final Long id);

    List<MakeupPageResponse> searchMakeup(final int pageSize, final int pageNumber);

    List<MakeupPageResponse> searchMakeupsByFilter(List<MakeupTag> tags, Category category,
        Region region, Integer minPrice, Integer maxPrice, SortType sortType, Integer pageNumber,
        Integer pageSize);

    Long countMakeupsByFilter(List<MakeupTag> tags, Category category, Region region,
        Integer minPrice, Integer maxPrice);
}
