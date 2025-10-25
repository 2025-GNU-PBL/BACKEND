package gnu.project.backend.payment.repository;

import gnu.project.backend.payment.dto.response.PaymentSettlementResponse;

import java.util.List;

public interface PaymentRepositoryDsl {

    List<PaymentSettlementResponse> findSettlementByOwner(Long ownerId);

}
