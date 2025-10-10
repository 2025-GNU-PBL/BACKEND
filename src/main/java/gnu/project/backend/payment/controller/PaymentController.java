package gnu.project.backend.payment.controller;


import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.request.PaymentConfirmRequest;
import gnu.project.backend.payment.dto.response.*;
import gnu.project.backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @Auth final Accessor accessor,
            @RequestBody final PaymentConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.confirmPayment(accessor, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(
            @Auth final Accessor accessor,
            @RequestBody final PaymentCancelRequest request
    ) {
        PaymentCancelResponse response = paymentService.cancelPayment(accessor, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<PaymentListResponse>> getPaymentHistory(@Auth Accessor accessor) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(accessor.getSocialId()));
    }

    @GetMapping("/detail/{paymentKey}")
    public ResponseEntity<PaymentDetailResponse> getPaymentDetail(@PathVariable String paymentKey) {
        return ResponseEntity.ok(paymentService.getPaymentDetail(paymentKey));
    }

    @GetMapping("/settlement/{ownerId}")
    public List<PaymentSettlementResponse> getSettlementByOwner(@PathVariable Long ownerId) {
        return paymentService.getSettlementByOwner(ownerId);
    }
}
