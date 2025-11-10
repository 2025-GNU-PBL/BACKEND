package gnu.project.backend.payment.repository;

import gnu.project.backend.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {

    Optional<Payment> findByPaymentKey(String paymentKey);

    List<Payment> findByOrder_Customer_OauthInfo_SocialId(String socialId);
}
