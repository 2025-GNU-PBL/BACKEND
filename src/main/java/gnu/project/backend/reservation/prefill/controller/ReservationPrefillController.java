package gnu.project.backend.reservation.prefill.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.reservation.dto.response.ReservationResponseDto;
import gnu.project.backend.reservation.prefill.controller.docs.ReservationPrefillDocs;
import gnu.project.backend.reservation.prefill.dto.request.ReservationFromDraftRequest;
import gnu.project.backend.reservation.prefill.dto.response.ReservationDraftBatchResponse;
import gnu.project.backend.reservation.prefill.dto.response.ReservationPrefillResponse;
import gnu.project.backend.reservation.prefill.service.ReservationPrefillService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inquiries")
public class ReservationPrefillController implements ReservationPrefillDocs {

    private final ReservationPrefillService prefillService;
    private final CustomerRepository customerRepository;

    @Override
    @GetMapping("/drafts/{id}")
    public ResponseEntity<ReservationPrefillResponse> getDraft(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable Long id
    ) {
        Long customerId = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION))
                .getId();

        return ResponseEntity.ok(prefillService.getPrefill(id, customerId));
    }

    @Override
    @PostMapping("/from-draft")
    public ResponseEntity<ReservationResponseDto> createFromDraft(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @Valid @RequestBody ReservationFromDraftRequest request
    ) {
        Customer customer = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));

        return ResponseEntity.ok(
                prefillService.consumeToReservation(request.prefillId(), customer, request.title(), request.content())
        );
    }

    @Override
    @PostMapping("/prefill-from-cart/selected")
    public ResponseEntity<ReservationDraftBatchResponse> createDraftsFromSelected(
            @Parameter(hidden = true) @Auth Accessor accessor
    ) {
        ReservationDraftBatchResponse body = prefillService.createDraftsFromSelectedCart(accessor);
        return ResponseEntity.ok(body);
    }
}
