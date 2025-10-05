package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.DressPageResponse;
import gnu.project.backend.product.dto.response.DressResponse;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface DressCustomRepository {

    DressResponse findByDressId(final Long id);

    List<DressPageResponse> searchDress(final int pageSize, final int pageNumber);

}
