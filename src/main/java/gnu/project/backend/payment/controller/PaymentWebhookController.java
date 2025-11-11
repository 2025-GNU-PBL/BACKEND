package gnu.project.backend.payment.controller;

import gnu.project.backend.payment.service.PaymentWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/toss")
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;


    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String rawBody) {
        try {
            paymentWebhookService.processWebhook(rawBody);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("[TOSS_WEBHOOK] Exception while processing webhook", e);
            // Toss는 항상 200 응답을 기대함 → 내부 오류여도 OK 반환
            return ResponseEntity.ok("RECEIVED_WITH_ERROR");
        }
    }
}
