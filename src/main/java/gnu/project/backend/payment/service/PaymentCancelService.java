package gnu.project.backend.payment.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.response.PaymentCancelResponse;
import gnu.project.backend.payment.dto.response.TossPaymentCancelReponse;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static gnu.project.backend.common.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    @Transactional
    public PaymentCancelResponse requestCancel(String socialId, PaymentCancelRequest request) {
        Payment payment = paymentRepository.findWithOrderAndDetailsByPaymentKey(request.paymentKey())
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        String customerSocialId = payment.getOrder().getCustomerSocialId();
        if (!socialId.equals(customerSocialId)) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        payment.requestCancel(request.cancelReason());

        paymentRepository.save(payment);
        return PaymentCancelResponse.from(payment);
    }

    public PaymentCancelResponse approveCancel(Accessor accessor, String paymentKey) {
        Payment payment = paymentRepository.findWithOrderAndDetailsByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        String ownerSocialId = payment.getOrder().getMainProductOwnerSocialId();
        if (!accessor.isOwner() || ownerSocialId == null || !ownerSocialId.equals(accessor.getSocialId())) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        if (payment.getStatus() != PaymentStatus.CANCEL_REQUESTED) {
            return PaymentCancelResponse.from(payment);
        }

        TossPaymentCancelReponse toss = tossPaymentClient.cancelPayment(
                paymentKey,
                payment.getCancelReason(),
                payment.getAmount()
        );

        approveCancelInternal(payment, toss);

        return PaymentCancelResponse.from(payment);
    }

    @Transactional
    protected void approveCancelInternal(Payment payment, TossPaymentCancelReponse toss) {
        payment.approveCancel(toss.getCancelReason(), LocalDateTime.now());
        paymentRepository.save(payment);
    }
}
