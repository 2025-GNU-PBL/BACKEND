package gnu.project.backend.customer.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.customer.dto.request.CustomerRequest;
import gnu.project.backend.customer.dto.response.CustomerResponse;
import gnu.project.backend.customer.dto.response.CustomerSignInResponse;
import gnu.project.backend.customer.dto.response.CustomerUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Tag(
        name = "Customer API",
        description = "일반 사용자(Customer) 정보 관리, 재가입, 탈퇴 API"
)
public interface CustomerDocs {
    @Operation(
            summary = "회원 정보 등록 / 재가입",
            description = """
            소셜 로그인으로 최초 생성된 Customer에 대해
            추가 정보(age, phoneNumber, address)를 등록하거나,
            탈퇴했던 계정을 다시 활성화(re-activate)합니다.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "등록 또는 재가입 성공",
                            content = @Content(schema = @Schema(implementation = CustomerSignInResponse.class))
                    )
            }
    )
    @PostMapping
    ResponseEntity<CustomerSignInResponse> signUp(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @Parameter(description = "온보딩/재가입 시 입력 정보", required = true)
            @RequestBody CustomerRequest request
    );

    @Operation(
            summary = "회원 정보 수정",
            description = "현재 활성 상태인 고객의 age/phoneNumber/address를 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 완료",
                            content = @Content(schema = @Schema(implementation = CustomerUpdateResponse.class))
                    )
            }
    )
    @PatchMapping
    ResponseEntity<CustomerUpdateResponse> update(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @Parameter(description = "수정할 회원 정보", required = true)
            @RequestBody CustomerRequest request
    );

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 고객의 정보를 조회합니다. 이미 탈퇴한 계정이면 에러가 반환됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = CustomerResponse.class))
                    )
            }
    )
    @GetMapping
    ResponseEntity<CustomerResponse> find(
            @Parameter(hidden = true) @Auth Accessor accessor
    );

    @Operation(
            summary = "회원 탈퇴",
            description = """
            소프트 탈퇴 처리합니다.
            - isDeleted = true
            - 개인정보(age, phoneNumber, address) 초기화
            같은 계정으로 나중에 다시 가입하려면 POST /api/v1/customer 를 호출하면 됩니다.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "탈퇴 성공 (응답 바디 없음)"
                    )
            }
    )

    @DeleteMapping
    ResponseEntity<Void> withdraw(
            @Parameter(hidden = true) @Auth Accessor accessor
    );
}
