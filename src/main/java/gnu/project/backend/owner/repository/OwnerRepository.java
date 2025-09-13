package gnu.project.backend.owner.repository;

import gnu.project.backend.owner.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

}
