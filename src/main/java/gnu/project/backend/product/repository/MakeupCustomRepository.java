package gnu.project.backend.product.repository;

import gnu.project.backend.product.dto.response.MakeupPageResponse;
import gnu.project.backend.product.dto.response.MakeupResponse;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface MakeupCustomRepository {


    MakeupResponse findByMakeupId(final Long id);

    List<MakeupPageResponse> searchMakeup(final int pageSize, final int pageNumber);
}
