package gnu.project.backend.payment.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.OrderStatus;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.order.entity.Order;
import gnu.project.backend.order.repository.OrderRepository;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.request.PaymentConfirmRequest;
import gnu.project.backend.payment.dto.response.*;
import gnu.project.backend.payment.entity.Payment;
import gnu.project.backend.payment.repository.PaymentRepository;
import gnu.project.backend.payment.repository.PaymentRepositoryDsl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static gnu.project.backend.common.error.ErrorCode.*;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    // private final WebClient webClient;
    private final PaymentRepositoryDsl paymentRepositoryDsl;


    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Value("${payment.toss.confirm-url}")
    private String tossConfirmUrl;

    @Value("${payment.toss.base-url}")
    private String tossBaseUrl;





    public PaymentConfirmResponse confirmPayment(Accessor accessor, PaymentConfirmRequest request){
        // 1. 주문서(Order) 조회 및 검증

        log.info("🛎 confirmPayment() 호출 - accessor: {}, request: {}", accessor.getSocialId(), request);
        Order order = orderRepository.findByOrderCode(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        // 검증 1: 주문한 사람이 현재 로그인한 사용자인지 확인
        if (!order.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }
        // 검증 2: 프론트에서 받은 결제 금액과 DB에 저장된 주문 금액이 일치하는지 확인
        if (!order.getTotalPrice().equals(request.getAmount())) {
            throw new BusinessException(PAYMENT_AMOUNT_MISMATCH);
        }

        // 2. 토스페이먼츠에 최종 결제 승인 요청
        WebClient tossWebClient = WebClient.builder()
                .baseUrl(tossBaseUrl)
                .build();

        String secretKey = tossSecretKey + ":";
        String encodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));


        // ✅ Map을 사용하여 요청 Body를 명시적으로 구성합니다.
        Map<String, Object> requestBody = Map.of(
                "paymentKey", request.getPaymentKey(),
                "orderId", request.getOrderId(),
                "amount", request.getAmount()
        );
        log.info("🧾 토스 결제 요청 준비 - body: {}", requestBody);
        log.info("🔑 토스 인코딩 키: {}", encodedKey);


        TossPaymentConfirmResponse tossResponse = tossWebClient.post()
                .uri(tossConfirmUrl)
                .header("Authorization", "Basic " + encodedKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchangeToMono(clientResponse -> {
                    // 상태 코드 로그
                    log.info("🔔 Toss API Response Status: {}", clientResponse.statusCode());

                    // 오류 발생 시 body 로그
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .doOnNext(body -> log.error("⚠️ Toss API Error Body: {}", body))
                                .flatMap(body -> Mono.error(new BusinessException(PAYMENT_CONFIRM_FAILED)));
                    }

                    // 정상 응답이면 DTO로 변환
                    return clientResponse.bodyToMono(TossPaymentConfirmResponse.class)
                            .doOnNext(body -> log.info("✅ Toss API Response Body: {}", body));
                })
                .block();


        log.info("🧾 [TOSS REQUEST BODY] paymentKey={}, orderId={}, amount={}",
                request.getPaymentKey(), request.getOrderId(), request.getAmount());
        // 3. 결제 정보(Payment) 생성 및 저장
        Payment payment = Payment.create(order, tossResponse); // Entity에 create 정적 메서드 추가 필요
        paymentRepository.save(payment);

        // 4. 주문(Order) 상태 업데이트
         order.updateStatus(OrderStatus.PAID);

        // 5. 예약(Reservation) 상태 업데이트 (ReservationService 호출)
        //reservationService.confirmReservation(order.getReservationId());
        log.info("🛎 confirmPayment() 호출 - accessor: {}, request: {}", accessor.getSocialId(), request);
        // 임시 반환값, 2~5번 구현 후 실제 데이터로 교체 필요
        return new PaymentConfirmResponse(payment);

    }

    @Transactional
    public PaymentCancelResponse cancelPayment(Accessor accessor, PaymentCancelRequest request) {
        log.info("🧾 cancelPayment() 호출 - accessor: {}, request: {}", accessor.getSocialId(), request);

        // 1️⃣ 결제 정보 조회 및 검증
        Payment payment = paymentRepository.findByPaymentKey(request.getPaymentKey())
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        Order order = payment.getOrder();

        // 검증 1: 결제 요청자와 로그인 유저가 같은지
        if (!order.getCustomer().getOauthInfo().getSocialId().equals(accessor.getSocialId())) {
            throw new BusinessException(PAYMENT_ACCESS_DENIED);
        }

        // 검증 2: 이미 취소된 결제는 불가
        if (Objects.equals(payment.getStatus(), "CANCELED")) {
            throw new BusinessException(PAYMENT_ALREADY_CANCELED);
        }

        // 2️⃣ Toss API 호출 준비
        String encodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        WebClient tossWebClient = WebClient.builder()
                .baseUrl(tossBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Map<String, Object> cancelBody = Map.of(
                "cancelReason", request.getCancelReason(),
                "cancelAmount", request.getCancelAmount() != null ? request.getCancelAmount() : payment.getAmount()
        );

        log.info("🚨 [TOSS CANCEL REQUEST] paymentKey={}, body={}", payment.getPaymentKey(), cancelBody);

        TossPaymentCancelReponse tossResponse = tossWebClient.post()
                .uri("/v1/payments/" + payment.getPaymentKey() + "/cancel")
                .bodyValue(cancelBody)
                .exchangeToMono(response -> {
                    log.info("📡 Toss Cancel Response Status: {}", response.statusCode());
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .doOnNext(body -> log.error("⚠️ Toss Cancel API Error: {}", body))
                                .flatMap(body -> Mono.error(new BusinessException(PAYMENT_CANCEL_FAILED)));
                    }
                    return response.bodyToMono(TossPaymentCancelReponse.class)
                            .doOnNext(body -> log.info("✅ Toss Cancel API Response: {}", body));
                })
                .block();

        // 3️⃣ Payment 상태 업데이트
        payment.updateCancelStatus(
                tossResponse.getStatus(),
                tossResponse.getCancelReason(),
                tossResponse.getCanceledAt()
        );

        // 4️⃣ Order 상태 업데이트
         order.updateStatus(OrderStatus.CANCELLED);

        // 5️⃣ DB 저장
        paymentRepository.save(payment);

        log.info("✅ 결제 취소 완료 - orderCode={}, cancelReason={}", order.getOrderCode(), request.getCancelReason());

        // 6️⃣ 최종 응답 반환
        return new PaymentCancelResponse(payment);
    }

    // 소비자 결제 내역 조회
    public List<PaymentListResponse> getPaymentHistory(String socialId) {
        return paymentRepository.findByOrder_Customer_SocialId(socialId)
                .stream()
                .map(p -> {
                    // 첫 번째 OrderDetail 상품명 사용
                    String productName = p.getOrder().getOrderDetails().isEmpty()
                            ? "상품 없음"
                            : p.getOrder().getOrderDetails().get(0).getProduct().getName();

                    return new PaymentListResponse(
                            p.getOrder().getOrderCode(),
                            productName,
                            p.getAmount(),
                            p.getStatus(),
                            p.getApprovedAt()
                    );
                })
                .toList();
    }

    // 결제 상세 조회
    public PaymentDetailResponse getPaymentDetail(String paymentKey) {
        Payment p = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        String productName = p.getOrder().getOrderDetails().isEmpty()
                ? "상품 없음"
                : p.getOrder().getOrderDetails().get(0).getProduct().getName();

        return new PaymentDetailResponse(
                p.getOrder().getOrderCode(),
                productName,
                p.getAmount(),
                p.getStatus(),
                p.getApprovedAt(),
                p.getCanceledAt(),
                p.getCancelReason(),
                p.getReceiptUrl(),
                p.getPaymentMethod(),
                p.getPgProvider()
        );
    }

    public List<PaymentSettlementResponse> getSettlementByOwner(Long ownerId) {
        return paymentRepositoryDsl.findSettlementByOwner(ownerId);
    }

}
