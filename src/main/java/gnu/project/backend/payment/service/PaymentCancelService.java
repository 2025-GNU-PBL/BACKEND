package gnu.project.backend.payment.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.response.PaymentCancelResponse;
import gnu.project.backend.payment.dto.response.TossPaymentCancelReponse;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

import static gnu.project.backend.common.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelService {

    private final PaymentRepository paymentRepository;
    private final PaymentRefundService paymentRefundService;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Value("${payment.toss.base-url}")
    private String tossBaseUrl;

    @Transactional
    public PaymentCancelResponse requestCancel(String socialId, PaymentCancelRequest request) {
        Payment payment = paymentRepository.findByPaymentKey(request.paymentKey())
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        if (!payment.getOrder().getCustomer().getOauthInfo().getSocialId().equals(socialId)) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        payment.requestCancel(request.cancelReason());
        paymentRepository.save(payment);

        return PaymentCancelResponse.from(payment);
    }

    public PaymentCancelResponse approveCancel(Accessor accessor, String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        // 사장 권한 체크
        String ownerSocialId = payment.getOrder().getOrderDetails().get(0)
                .getProduct().getOwner().getOauthInfo().getSocialId();
        if (!accessor.isOwner() || !ownerSocialId.equals(accessor.getSocialId())) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        if (payment.getStatus() != PaymentStatus.CANCEL_REQUESTED) {
            // 이미 처리된 케이스는 그냥 성공 응답
            return PaymentCancelResponse.from(payment);
        }

        // 1) PG 취소 호출
        TossPaymentCancelReponse toss = callTossCancel(paymentKey, payment.getCancelReason(), payment.getAmount());

        // 2) 내부 상태 변경 + 정산
        approveAndRefund(payment, toss);

        return PaymentCancelResponse.from(payment);
    }

    @Transactional
    protected void approveAndRefund(Payment payment, TossPaymentCancelReponse toss) {
        payment.approveCancel(toss.getCancelReason(), LocalDateTime.now());
        paymentRepository.save(payment);

        // 내부 정산은 따로
        paymentRefundService.processRefund(payment.getPaymentKey());
    }

    private TossPaymentCancelReponse callTossCancel(String paymentKey, String reason, Long amount) {
        String encodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

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
