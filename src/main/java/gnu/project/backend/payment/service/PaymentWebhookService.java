package gnu.project.backend.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.order.service.OrderService;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static gnu.project.backend.common.error.ErrorCode.PAYMENT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processWebhook(String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);

            String eventType = root.path("eventType").asText();
            if (!"PAYMENT_STATUS_CHANGED".equals(eventType)) {
                log.info("[TOSS_WEBHOOK] Ignored eventType: {}", eventType);
                return;
            }

            JsonNode data = root.path("data");
            String paymentKey = data.path("paymentKey").asText();
            String orderCode = data.path("orderId").asText();
            String statusStr = data.path("status").asText();

            log.info("[TOSS_WEBHOOK] Received: paymentKey={}, orderCode={}, status={}",
                    paymentKey, orderCode, statusStr);

            Payment payment = paymentRepository.findWithOrderAndDetailsByPaymentKey(paymentKey)
                    .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

            PaymentStatus before = payment.getStatus();
            PaymentStatus newStatus = PaymentStatus.fromString(statusStr);

            payment.setStatus(newStatus);
            paymentRepository.save(payment);

            if (newStatus == PaymentStatus.DONE) {
                log.info("[TOSS_WEBHOOK] Call onPaymentConfirmed. beforeStatus={}, paymentKey={}, orderCode={}",
                        before, paymentKey, orderCode);

                orderService.onPaymentConfirmed(orderCode);
            } else {
                log.info("[TOSS_WEBHOOK] Payment status changed: {} -> {} (no onPaymentConfirmed)",
                        before, newStatus);
            }

        } catch (Exception e) {
            log.error("[TOSS_WEBHOOK] Failed to process webhook body: {}", rawBody, e);
        }
    }
}
