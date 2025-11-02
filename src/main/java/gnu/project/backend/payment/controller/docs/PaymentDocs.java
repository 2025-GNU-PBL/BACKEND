package gnu.project.backend.payment.controller.docs;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.payment.dto.request.PaymentCancelRequest;
import gnu.project.backend.payment.dto.request.PaymentConfirmRequest;
import gnu.project.backend.payment.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Payment", description = "결제 API")
public interface PaymentDocs {

    @Operation(summary = "결제 승인", description = "토스 결제 승인 후 우리 시스템에 반영")
    ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @Parameter(hidden = true) Accessor accessor,
            @RequestBody PaymentConfirmRequest request
    );

    @Operation(summary = "결제 취소 요청(고객)", description = "고객이 자신의 결제를 취소 요청")
    ResponseEntity<PaymentCancelResponse> requestCancel(
            @Parameter(hidden = true) Accessor accessor,
            @RequestBody PaymentCancelRequest request
    );

    @Operation(summary = "결제 취소 승인(사장)", description = "사장이 고객의 취소 요청을 승인하고 PG에도 취소 요청")
    ResponseEntity<PaymentCancelResponse> approveCancel(
            @Parameter(hidden = true) Accessor accessor,
            @PathVariable String paymentKey
    );

    @Operation(summary = "내 결제 내역", description = "로그인한 고객의 결제 목록 조회")
    ResponseEntity<List<PaymentListResponse>> getMyPayments(
            @Parameter(hidden = true) Accessor accessor
    );

    @Operation(summary = "결제 상세", description = "고객/사장만 접근 가능한 결제 상세 조회")
    ResponseEntity<PaymentDetailResponse> getPaymentDetail(
            @Parameter(hidden = true) Accessor accessor,
            @PathVariable String paymentKey
    );

    @Operation(summary = "내 정산 내역(사장)", description = "사장 계정으로 자신의 정산 목록 조회")
    ResponseEntity<List<PaymentSettlementResponse>> getMySettlements(
            @Parameter(hidden = true) Accessor accessor
    );
}
