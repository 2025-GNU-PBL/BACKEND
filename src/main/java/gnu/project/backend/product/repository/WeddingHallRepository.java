package gnu.project.backend.product.repository;

import gnu.project.backend.product.entity.WeddingHall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeddingHallRepository extends JpaRepository<WeddingHall, Long>, WeddingHallCustomRepository {

}
