package gnu.project.backend.product.repository;

import gnu.project.backend.product.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
