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
import org.springframework.http.HttpHeaders;
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
    private final PaymentService paymentService;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Value("${payment.toss.base-url}")
    private String tossBaseUrl;

    @Value("${payment.toss.confirm-url}")
    private String tossConfirmUrl;

    public PaymentConfirmResponse confirmPayment(Accessor accessor, PaymentConfirmRequest request) {

        Order order = orderRepository.findByOrderCode(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        if (!order.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        if (!order.getTotalPrice().equals(request.getAmount())) {
            throw new BusinessException(PAYMENT_AMOUNT_MISMATCH);
        }

        //(트랜잭션 밖)
        TossPaymentConfirmResponse tossResponse = requestTossPaymentConfirm(request);

        //(트랜잭션 안)
        Payment payment = savePaymentAndUpdateOrder(order, tossResponse);

        log.info("결제 승인 완료 - 주문번호: {}, 결제상태: {}", order.getOrderCode(), payment.getStatus());
        return new PaymentConfirmResponse(payment);
    }


    @Transactional
    protected Payment savePaymentAndUpdateOrder(Order order, TossPaymentConfirmResponse tossResponse) {
        Payment payment = Payment.create(order, tossResponse);
        order.updateStatus(mapOrderStatusByPayment(payment.getStatus()));

        paymentRepository.save(payment);
        orderRepository.save(order);
        return payment;
    }


    private TossPaymentConfirmResponse requestTossPaymentConfirm(PaymentConfirmRequest request) {
        String encodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        return WebClient.builder()
                .baseUrl(tossBaseUrl)
                .build()
                .post()
                .uri(tossConfirmUrl)
                .header("Authorization", "Basic " + encodedKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "paymentKey", request.getPaymentKey(),
                        "orderId", request.getOrderId(),
                        "amount", request.getAmount()
                ))
                .retrieve()
                .bodyToMono(TossPaymentConfirmResponse.class)
                .block();
    }

    private OrderStatus mapOrderStatusByPayment(PaymentStatus status) {
        return switch (status) {
            case DONE -> OrderStatus.PAID;
            case CANCELED -> OrderStatus.CANCELED;
            default -> OrderStatus.PAYMENT_FAILED;
        };
    }

}

