package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.StudioPageResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import gnu.project.backend.product.entity.Studio;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface StudioCustomRepository {

    StudioResponse findByStudioId(final Long id);

    List<StudioPageResponse> searchStudio(final int pageSize, final int pageNumber);

    Optional<Studio> findStudioWithImagesAndOptionsById(final Long id);

}
