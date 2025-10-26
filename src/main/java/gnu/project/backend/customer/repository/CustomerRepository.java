package gnu.project.backend.customer.repository;

import gnu.project.backend.customer.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 고객정보 BY Social ID
    Optional<Customer> findByOauthInfo_SocialId(final String socialId);

    // 고객정보 CHECK BY Social ID
    boolean existsByOauthInfo_SocialId(final String socialId);
}
