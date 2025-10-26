package gnu.project.backend.product.repository;

import gnu.project.backend.product.entity.Studio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudioRepository extends JpaRepository<Studio, Long>, StudioCustomRepository {

}
