package gnu.project.backend.payment.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.repository.OrderRepository;
import gnu.project.backend.payment.dto.request.PaymentConfirmRequest;
import gnu.project.backend.payment.dto.response.PaymentConfirmResponse;
import gnu.project.backend.payment.dto.response.TossPaymentConfirmResponse;
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
import java.util.Base64;
import java.util.Map;

import static gnu.project.backend.common.error.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConfirmService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Value("${payment.toss.base-url}")
    private String tossBaseUrl;

    @Value("${payment.toss.confirm-url}")
    private String tossConfirmUrl;

    public PaymentConfirmResponse confirm(Accessor accessor, PaymentConfirmRequest request) {

        Order order = orderRepository.findByOrderCodeWithDetails(request.orderId())
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        if (!order.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        if (!order.getTotalPrice().equals(request.amount())) {
            throw new BusinessException(PAYMENT_AMOUNT_MISMATCH);
        }

        // 외부(PG)
        TossPaymentConfirmResponse toss = callTossConfirm(request);

        // 내부(DB)
        Payment payment = savePaymentAndUpdateOrder(order, toss);

        log.info("결제 승인 완료: orderCode={}", order.getOrderCode());
        return PaymentConfirmResponse.from(payment);
    }

    @Transactional
    protected Payment savePaymentAndUpdateOrder(Order order, TossPaymentConfirmResponse toss) {
        // idem 보호
        if (order.getStatus() == OrderStatus.PAID) {
            return paymentRepository.findByPaymentKey(toss.getPaymentKey())
                    .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));
        }

        Payment payment = Payment.create(order, toss);
        order.updateStatus(mapOrderStatus(payment.getStatus()));

        paymentRepository.save(payment);
        orderRepository.save(order);
        return payment;
    }

    private TossPaymentConfirmResponse callTossConfirm(PaymentConfirmRequest req) {
        String encodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

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
                        "paymentKey", req.paymentKey(),
                        "orderId", req.orderId(),
                        "amount", req.amount()
                ))
                .retrieve()
                .bodyToMono(TossPaymentConfirmResponse.class)
                .block();
    }

    private OrderStatus mapOrderStatus(PaymentStatus status) {
        return switch (status) {
            case DONE -> OrderStatus.PAID;
            case CANCELED -> OrderStatus.CANCELED;
            default -> OrderStatus.PAYMENT_FAILED;
        };
    }
}
