package gnu.project.backend.payment.service;

import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static gnu.project.backend.common.error.ErrorCode.PAYMENT_NOT_FOUND;
import static gnu.project.backend.common.error.ErrorCode.PAYMENT_REFUND_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class PaymentRefundService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void processRefund(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.CANCELED &&
                payment.getStatus() != PaymentStatus.CANCEL_REQUESTED) {
            throw new BusinessException(PAYMENT_REFUND_NOT_ALLOWED);
        }

        payment.refund("PG 환불 완료", LocalDateTime.now());
        paymentRepository.save(payment);
    }
}
