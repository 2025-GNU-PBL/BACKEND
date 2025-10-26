package gnu.project.backend.product.repository;

import gnu.project.backend.product.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
