package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.MakeupResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface MakeupCustomRepository {

    MakeupResponse findByMakeupId(final Long id);
}
