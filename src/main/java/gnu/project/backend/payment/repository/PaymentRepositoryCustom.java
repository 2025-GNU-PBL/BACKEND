package gnu.project.backend.payment.repository;

import gnu.project.backend.payment.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepositoryCustom {

    // 고객 기준 결제 목록
    List<Payment> findAllWithOrderAndDetailsByCustomerSocialId(String socialId, int page, int size);

    // paymentKey 기준 단건 (웹훅/취소/상세 조회)
    Optional<Payment> findWithOrderAndDetailsByPaymentKey(String paymentKey);

    //사장(owner) 기준 정산 화면용 결제 목록
    List<Payment> findAllWithOrderAndDetailsByOwnerId(Long ownerId);

    //사장 취소 요청 목록 조회
    List<Payment> findAllCancelRequestedWithOrderAndDetailsByOwnerId(Long ownerId);

    //사장 취소 목록 조회
    List<Payment> findAllCanceledWithOrderAndDetailsByOwnerId(Long ownerId);
}
