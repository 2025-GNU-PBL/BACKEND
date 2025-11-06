package gnu.project.backend.reservation.prefill.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.reservation.dto.response.ReservationResponseDto;
import gnu.project.backend.reservation.prefill.dto.request.ReservationFromDraftRequest;
import gnu.project.backend.reservation.prefill.dto.response.ReservationDraftBatchResponse;
import gnu.project.backend.reservation.prefill.dto.response.ReservationPrefillResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Inquiry Prefill API", description = "문의 작성 프리필(draft) 조회/확정 API")
public interface ReservationPrefillDocs {
    @Operation(
            summary = "선택된 카트 아이템들로 프리필 일괄 생성",
            description = "장바구니에서 selected=true 인 항목들을 바탕으로 문의 프리필 여러 개를 생성하고, 생성된 draftIds를 반환합니다."
    )
    @PostMapping("/api/v1/inquiries/prefill-from-cart/selected")
    ResponseEntity<ReservationDraftBatchResponse> createDraftsFromSelected(
            @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(
            summary = "프리필 조회",
            description = """
        장바구니/상품 상세에서 생성된 프리필을 조회해 폼을 사전 채움.
        - Path: /api/v1/inquiries/drafts/{id}
        - 권한: 로그인 사용자(본인 소유 프리필만 조회 가능)
        - 응답: ReservationPrefillResponse(상품/수량/희망일/메모 등)
        """
    )
    @GetMapping("/api/v1/inquiries/drafts/{id}")
    ResponseEntity<ReservationPrefillResponse> getDraft(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable Long id
    );

    @Operation(
            summary = "프리필 기반 문의 확정 생성",
            description = """
        프리필을 소비하여 실제 문의(예약)를 생성.
        - Path: /api/v1/inquiries/from-draft
        - 입력: ReservationFromDraftRequest(prefillId, title, content)
        - 응답: ReservationResponseDto
        - 비고: 성공 시 해당 prefillId는 소모(재사용 불가)
        """
    )
    @PostMapping("/api/v1/inquiries/from-draft")
    ResponseEntity<ReservationResponseDto> createFromDraft(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @Valid @RequestBody ReservationFromDraftRequest request
    );
}
