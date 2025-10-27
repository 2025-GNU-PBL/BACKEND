package gnu.project.backend.payment.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.request.PaymentConfirmRequest;
import gnu.project.backend.payment.dto.request.PaymentRefundRequest;
import gnu.project.backend.payment.dto.response.*;
import gnu.project.backend.payment.service.PaymentCancelService;
import gnu.project.backend.payment.service.PaymentConfirmService;
import gnu.project.backend.payment.service.PaymentRefundService;
import gnu.project.backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentConfirmService paymentConfirmService;
    private final PaymentCancelService paymentCancelService;
    private final PaymentRefundService paymentRefundService;
    private final PaymentService paymentService; // 공통 조회/히스토리용

    // (고객용) 결제 승인
    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @Auth final Accessor accessor,
            @RequestBody final PaymentConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentConfirmService.confirmPayment(accessor, request);
        return ResponseEntity.ok(response);
    }
//    @PostMapping("/confirm")
//    public ResponseEntity<PaymentConfirmResponse> confirmPayment(@Auth Accessor accessor,
//                                                                 @RequestBody PaymentConfirmRequest request
//                                                                 ) {
//        return ResponseEntity.ok(paymentConfirmService.confirmPayment(accessor, request));
//    }

    // (고객용) 결제 취소 요청
    @PostMapping("/cancel-request")
    public ResponseEntity<PaymentCancelResponse> requestCancelPayment(@Auth Accessor accessor,
                                                                      @RequestBody PaymentCancelRequest request
                                                                      ) {
        return ResponseEntity.ok(paymentCancelService.requestCancel(accessor.getSocialId(), request));
    }

    // (사장님용) 결제 취소 승인
    @PostMapping("/{paymentKey}/cancel-approve")
    public ResponseEntity<PaymentCancelResponse> approveCancelPayment(@Auth Accessor accessor,
                                                                      @PathVariable String paymentKey) {
        // TODO: 서비스에서 accessor.isOwner() 등으로 사장님 권한 확인 필요
        return ResponseEntity.ok(paymentCancelService.approveCancel(paymentKey,accessor));
    }

    // (고객용) 내 결제 내역 조회
    @GetMapping("/me")
    public ResponseEntity<List<PaymentListResponse>> getMyPaymentHistory(@Auth Accessor accessor) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(accessor.getSocialId()));
    }

    // (공용) 결제 상세 조회
    @GetMapping("/{paymentKey}") //
    public ResponseEntity<PaymentDetailResponse> getPaymentDetail(
            @Auth Accessor accessor, //
            @PathVariable String paymentKey) {

        return ResponseEntity.ok(paymentService.getPaymentDetail(paymentKey,accessor));
    }

    // (사장님용) 내 정산 내역 조회
    @GetMapping("/settlements/me") // 로 변경
    public ResponseEntity<List<PaymentSettlementResponse>> getMyShopSettlement(@Auth Accessor accessor) {
        // TODO :사장 인증 로직 추가
        return ResponseEntity.ok(paymentService.getSettlementByOwner(Long.valueOf(accessor.getSocialId())));
    }
}
