package gnu.project.backend.product.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.MakeupRequest;
import gnu.project.backend.product.dto.request.MakeupUpdateRequest;
import gnu.project.backend.product.dto.response.MakeupPageResponse;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.service.MakeupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/makeup")
@Tag(name = "Makeup API", description = "메이크업 상품 관련 CRUD API")
public class MakeupController {

    private final MakeupService makeupService;

    @Operation(
        summary = "메이크업 상품 생성",
        description = "메이크업 상품을 생성하고 이미지를 업로드할 수 있습니다.",
        responses = {
            @ApiResponse(responseCode = "201", description = "메이크업 상품 생성 성공",
                content = @Content(schema = @Schema(implementation = MakeupResponse.class))),
        }
    )
    @PostMapping()
    public ResponseEntity<MakeupResponse> createMakeup(
        @Parameter(description = "메이크업 상품 요청 DTO", required = true)
        @Valid @RequestPart("request") final MakeupRequest request,

        @Parameter(description = "업로드할 이미지 파일 목록", required = false)
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,

        @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                makeupService.create(request, images, accessor)
            );
    }

    @Operation(
        summary = "메이크업 상품 수정",
        description = "기존 메이크업 상품 정보를 수정하고 이미지를 변경할 수 있습니다.",
        responses = {
            @ApiResponse(responseCode = "201", description = "메이크업 상품 수정 성공",
                content = @Content(schema = @Schema(implementation = MakeupResponse.class))),
        }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<MakeupResponse> updateMakeup(
        @Parameter(description = "수정할 상품 ID", required = true)
        @PathVariable(name = "id") final Long id,

        @Parameter(description = "메이크업 상품 수정 요청 DTO", required = true)
        @Valid @RequestPart("request") final MakeupUpdateRequest request,

        @Parameter(description = "업로드할 이미지 파일 목록", required = false)
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,

        @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                makeupService.update(id, request, images, accessor, request.keepImagesId())
            );
    }

    @Operation(
        summary = "메이크업 상품 단건 조회",
        description = "메이크업 상품의 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = MakeupResponse.class))),
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<MakeupResponse> readMakeup(
        @Parameter(description = "조회할 상품 ID", required = true)
        @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(makeupService.read(id));
    }

    @Operation(
        summary = "메이크업 상품 목록 조회",
        description = "페이지네이션을 이용해 메이크업 상품 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = MakeupPageResponse.class)))
        }
    )
    @GetMapping()
    public ResponseEntity<Page<MakeupPageResponse>> readMakeups(
        @Parameter(description = "페이지 번호 (기본값 1)")
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,

        @Parameter(description = "페이지 크기 (기본값 6)")
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(
            makeupService.readMakeups(pageNumber, pageSize)
        );
    }

    @Operation(
        summary = "메이크업 상품 삭제",
        description = "특정 메이크업 상품을 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMakeup(
        @Auth final Accessor accessor,
        @Parameter(description = "삭제할 상품 ID", required = true)
        @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(makeupService.delete(id, accessor));
    }

}
