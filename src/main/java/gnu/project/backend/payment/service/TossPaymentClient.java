package gnu.project.backend.payment.service;

import gnu.project.backend.payment.dto.response.TossPaymentCancelReponse;
import gnu.project.backend.payment.dto.response.TossPaymentConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Value("${payment.toss.base-url}")
    private String tossBaseUrl;

    @Value("${payment.toss.confirm-url}")
    private String tossConfirmUrl;

    private String encodedSecret() {
        return Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    public TossPaymentConfirmResponse confirmPayment(String paymentKey, String orderCode, Long amount) {
        String encodedKey = encodedSecret();

        return WebClient.builder()
                .baseUrl(tossBaseUrl)
                .build()
                .post()
                .uri(tossConfirmUrl)
                .headers(h -> {
                    h.add("Authorization", "Basic " + encodedKey);
                    h.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(Map.of(
                        "paymentKey", paymentKey,
                        "orderId", orderCode,
                        "amount", amount
                ))
                .retrieve()
                .bodyToMono(TossPaymentConfirmResponse.class)
                .block();
    }

    public TossPaymentCancelReponse cancelPayment(String paymentKey, String reason, Long amount) {
        String encodedKey = encodedSecret();

        return WebClient.builder()
                .baseUrl(tossBaseUrl)
                .build()
                .post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
                .headers(h -> {
                    h.add("Authorization", "Basic " + encodedKey);
                    h.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(Map.of(
                        "cancelReason", reason,
                        "cancelAmount", amount
                ))
                .retrieve()
                .bodyToMono(TossPaymentCancelReponse.class)
                .block();
    }
}
