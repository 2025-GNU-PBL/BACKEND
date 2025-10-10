package gnu.project.backend.payment.controller;


import gnu.project.backend.payment.service.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/webhook")
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;

    @PostMapping("/toss")
    public ResponseEntity<Void> handleTossWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader("Toss-Signature") String signature
    ) {
        log.info("🔔 Toss Webhook 수신: {}", payload);

        // 받은 웹훅 데이터와 시그니처를 서비스 로직으로 넘겨서 처리
        paymentWebhookService.processTossWebhook(payload, signature);

        // 토스페이먼츠 서버에는 항상 200 OK로 응답해야 합니다.
        return ResponseEntity.ok().build();
    }
}
