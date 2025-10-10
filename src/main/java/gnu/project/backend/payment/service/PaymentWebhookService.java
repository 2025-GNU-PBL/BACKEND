package gnu.project.backend.payment.service;


import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static gnu.project.backend.common.error.ErrorCode.PAYMENT_NOT_FOUND;
import static gnu.project.backend.common.error.ErrorCode.UNAUTHORIZED_WEBHOOK;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void processTossWebhook(Map<String, Object> payload, String signature) {
        // TODO: signature 검증 구현
        if (signature == null || !verifySignature(payload, signature)) {
            throw new BusinessException(UNAUTHORIZED_WEBHOOK);
        }

        String paymentKey = (String) payload.get("paymentKey");
        String status = (String) payload.get("status");

        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        switch (status) {
            case "DONE":
                payment.setStatus("DONE");
                payment.setApprovedAt(LocalDateTime.now());
                break;
            case "CANCELED":
                payment.setStatus("CANCELED");
                payment.setCanceledAt(LocalDateTime.now());
                payment.setCancelReason((String) payload.get("cancelReason"));
                break;
            default:
                log.warn("⚠️ 알 수 없는 웹훅 상태: {}", status);
        }

        paymentRepository.save(payment);
        log.info("✅ Payment updated via webhook: {}", payment.getId());
    }

    private boolean verifySignature(Map<String, Object> payload, String signature) {
        // TODO: 토스 HMAC-SHA256 검증 로직
        return true; // 테스트 시에는 임시로 true
    }

}
