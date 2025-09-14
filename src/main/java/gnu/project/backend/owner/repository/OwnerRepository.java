package gnu.project.backend.owner.repository;

import gnu.project.backend.owner.entity.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByOauthInfo_SocialId(final String socialId);
}
