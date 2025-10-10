package gnu.project.backend.payment.repository;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.response.PaymentCancelResponse;
import gnu.project.backend.payment.dto.response.PaymentSettlementResponse;
import gnu.project.backend.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentKey(String paymentKey);

    List<Payment> findByOrder_Customer_SocialId(String socialId);

//    @Query("SELECT new gnu.project.backend.payment.dto.response.PaymentSettlementResponse(p.order.product.name, SUM(p.amount), SUM(CASE WHEN p.status='CANCELED' THEN 0 ELSE p.amount END), COUNT(p)) " +
//            "FROM Payment p WHERE p.order.product.owner.id = :ownerId GROUP BY p.order.product.name")
//    List<PaymentSettlementResponse> findSettlementByOwner(Long ownerId);




}
