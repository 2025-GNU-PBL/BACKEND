package gnu.project.backend.payment.repository;

import gnu.project.backend.payment.dto.response.PaymentSettlementResponse;

import java.util.List;
import java.util.Optional;

public interface PaymentRepositoryCustom {

    List<PaymentSettlementResponse> findSettlementByOwnerId(Long ownerId);

    Optional<PaymentSettlementResponse> findOneForOwner(Long ownerId, String orderCode);
}
