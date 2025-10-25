package gnu.project.backend.payment.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.order.repository.OrderRepository;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.response.PaymentCancelResponse;
import gnu.project.backend.payment.dto.response.TossPaymentCancelReponse;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
    private final OrderRepository orderRepository;
    private final PaymentRefundService refundService;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Value("${payment.toss.base-url}")
    private String tossBaseUrl;


    @Transactional
    public PaymentCancelResponse requestCancel(String socialId, PaymentCancelRequest request) {
        Payment payment = paymentRepository.findByPaymentKey(request.getPaymentKey())
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        if (!payment.getOrder().getCustomer().getOauthInfo().getSocialId().equals(socialId))
            throw new BusinessException(PAYMENT_ACCESS_DENIED);

        payment.requestCancel(request.getCancelReason());
        paymentRepository.save(payment);

        log.info("고객 취소 요청 완료 - 주문번호: {}, 상태: {}", payment.getOrder().getOrderCode(), payment.getStatus());
        return new PaymentCancelResponse(payment);
    }


    public PaymentCancelResponse approveCancel(String paymentKey, Accessor accessor) {

        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        if (!accessor.isOwner()) {
            throw new BusinessException(AUTH_FORBIDDEN);
        }
        String ownerSocialId = payment.getOrder().getOrderDetails().get(0).getProduct().getOwner().getOauthInfo().getSocialId();

        if (!ownerSocialId.equals(accessor.getSocialId())) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        if (payment.getStatus() != PaymentStatus.CANCEL_REQUESTED)
            throw new BusinessException(PAYMENT_REFUND_NOT_ALLOWED);

        //(트랜잭션 밖)
        TossPaymentCancelReponse tossResponse = requestTossPaymentCancel(
                payment.getPaymentKey(),
                payment.getCancelReason(),
                payment.getAmount()
        );

        //트랜잭션 안에서
        approveCancelAndRefund(payment, tossResponse);

        log.info("취소 승인 완료 - 주문번호: {}, 상태: {}", payment.getOrder().getOrderCode(), payment.getStatus());
        return new PaymentCancelResponse(payment);
    }

    // 트랜잭션 안에서 내부 상태 변경 및 정산 처리
    @Transactional
    protected void approveCancelAndRefund(Payment payment, TossPaymentCancelReponse tossResponse) {
        payment.approveCancel(tossResponse.getCancelReason(), LocalDateTime.now());
        refundService.processRefund(payment.getPaymentKey());
        paymentRepository.save(payment);
    }


    private TossPaymentCancelReponse requestTossPaymentCancel(String paymentKey, String reason, Long amount) {
        Map<String, Object> cancelBody = Map.of(
                "cancelReason", reason,
                "cancelAmount", amount
        );

        String encodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        try {
            TossPaymentCancelReponse response = WebClient.builder()
                    .baseUrl(tossBaseUrl)
                    .build()
                    .post()
                    .uri("/v1/payments/" + paymentKey + "/cancel")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(cancelBody)
                    .retrieve()
                    .bodyToMono(TossPaymentCancelReponse.class)
                    .block();

            log.info("[Toss 결제 취소 성공] paymentKey={}, reason={}", paymentKey, reason);
            return response;

        } catch (Exception e) {
            log.error("[Toss 결제 취소 실패] paymentKey={}, reason={}, error={}", paymentKey, reason, e.getMessage());
            throw new BusinessException(PAYMENT_GATEWAY_ERROR);
        }
    }
}

