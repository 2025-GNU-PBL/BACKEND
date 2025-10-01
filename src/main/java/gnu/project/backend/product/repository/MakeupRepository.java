package gnu.project.backend.product.repository;

import gnu.project.backend.product.entity.Makeup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MakeupRepository extends JpaRepository<Makeup, Long>, MakeupCustomRepository {

}
