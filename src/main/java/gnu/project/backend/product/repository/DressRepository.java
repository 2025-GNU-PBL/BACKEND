package gnu.project.backend.product.repository;

import gnu.project.backend.product.entity.Dress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DressRepository extends JpaRepository<Dress, Long>, DressCustomRepository {


}
