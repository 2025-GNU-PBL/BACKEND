package gnu.project.backend.payment.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.PaymentStatus;
import gnu.project.backend.payment.controller.docs.PaymentDocs;
import gnu.project.backend.payment.dto.request.PaymentCancelRejectRequest;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.request.PaymentConfirmRequest;
import gnu.project.backend.payment.dto.response.*;
import gnu.project.backend.payment.service.PaymentCancelService;
import gnu.project.backend.payment.service.PaymentConfirmService;
import gnu.project.backend.payment.service.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController implements PaymentDocs {

    private final PaymentConfirmService paymentConfirmService;
    private final PaymentCancelService paymentCancelService;
    private final PaymentQueryService paymentQueryService;

    @Override
    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @Auth Accessor accessor,
            @RequestBody PaymentConfirmRequest request
    ) {
        return ResponseEntity.ok(paymentConfirmService.confirm(accessor, request));
    }

    @Override
    @PostMapping("/cancel-request")
    public ResponseEntity<PaymentCancelResponse> requestCancel(
            @Auth Accessor accessor,
            @RequestBody PaymentCancelRequest request
    ) {
        return ResponseEntity.ok(paymentCancelService.requestCancel(accessor.getSocialId(), request));
    }

    @Override
    @GetMapping("/cancel-requests/me")
    public ResponseEntity<List<PaymentCancelResponse>> getMyCancelRequests(
            @Auth Accessor accessor
    ) {
        return ResponseEntity.ok(paymentQueryService.getMyCancelRequests(accessor));
    }

    @Override
    @PostMapping("/{paymentKey}/cancel-approve")
    public ResponseEntity<PaymentCancelResponse> approveCancel(
            @Auth Accessor accessor,
            @PathVariable String paymentKey
    ) {
        return ResponseEntity.ok(paymentCancelService.approveCancel(accessor, paymentKey));
    }

    @Override
    @PostMapping("/{paymentKey}/cancel-reject")
    public ResponseEntity<PaymentCancelResponse> rejectCancel(
            @Auth Accessor accessor,
            @PathVariable String paymentKey,
            @RequestBody PaymentCancelRejectRequest request
            ) {
        return ResponseEntity.ok(paymentCancelService.rejectCancel(accessor, paymentKey, request.rejectReason()));
    }

    @Override
    @GetMapping("/cancels/me")
    public ResponseEntity<List<PaymentCancelResponse>> getMyCanceledPayments(
            @Auth Accessor accessor
    ) {
        return ResponseEntity.ok(paymentQueryService.getMyCanceledPayments(accessor));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<List<PaymentListResponse>> getMyPayments(
            @Auth Accessor accessor,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(
                paymentQueryService.getMyPayments(accessor.getSocialId(), page, size)
        );
    }

    @Override
    @GetMapping("/{paymentKey}")
    public ResponseEntity<PaymentDetailResponse> getPaymentDetail(
            @Auth Accessor accessor,
            @PathVariable String paymentKey
    ) {
        return ResponseEntity.ok(paymentQueryService.getDetail(paymentKey, accessor));
    }

    @Override
    @GetMapping("/settlements/me")
    public ResponseEntity<PaymentSettlementResponse> getMySettlements(
            @Auth Accessor accessor,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(
                paymentQueryService.getMySettlement(accessor, year, month, status, page, size)
        );
    }


}
