package gnu.project.backend.owner.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.dto.UploadImageDto;
import gnu.project.backend.owner.dto.request.OwnerRequest;
import gnu.project.backend.owner.dto.response.OwnerResponse;
import gnu.project.backend.owner.dto.response.OwnerSignInResponse;
import gnu.project.backend.owner.dto.response.OwnerUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Owner API", description = "소유자 관련 API 및 프로필 이미지 관리")
public interface OwnerDocs {

    @Operation(
        summary = "소유자 회원가입 / 로그인",
        description = "Accessor 인증 후 소유자 회원가입 또는 로그인 처리",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "회원가입/로그인 성공",
                content = @Content(schema = @Schema(implementation = OwnerSignInResponse.class))
            )
        }
    )
    @PostMapping
    ResponseEntity<OwnerSignInResponse> signIn(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Parameter(description = "회원가입 또는 로그인 요청 DTO", required = true)
        @RequestBody OwnerRequest signInRequest
    );

    @Operation(
        summary = "프로필 이미지 업로드",
        description = "Accessor 인증 후 소유자 프로필 이미지 업로드",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "이미지 업로드 성공",
                content = @Content(schema = @Schema(implementation = UploadImageDto.class))
            )
        }
    )
    @PostMapping("/profile/image")
    ResponseEntity<UploadImageDto> uploadImage(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Parameter(description = "업로드할 이미지 파일", required = true)
        @RequestParam("file") MultipartFile file
    );

    @Operation(
        summary = "소유자 정보 수정",
        description = "Accessor 인증 후 소유자 정보 업데이트",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "소유자 정보 수정 성공",
                content = @Content(schema = @Schema(implementation = OwnerUpdateResponse.class))
            )
        }
    )
    @PatchMapping
    ResponseEntity<OwnerUpdateResponse> update(
        @Parameter(hidden = true) @Auth Accessor accessor,
        @Parameter(description = "소유자 정보 수정 요청 DTO", required = true)
        @RequestBody OwnerRequest updateRequest
    );

    @Operation(
        summary = "소유자 정보 조회",
        description = "Accessor 인증 후 소유자 정보 조회",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = OwnerResponse.class))
            )
        }
    )
    @GetMapping
    ResponseEntity<OwnerResponse> find(
        @Parameter(hidden = true) @Auth Accessor accessor
    );
}
