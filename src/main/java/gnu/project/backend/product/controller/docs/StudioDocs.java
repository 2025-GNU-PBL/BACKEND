package gnu.project.backend.product.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.StudioRequest;
import gnu.project.backend.product.dto.request.StudioUpdateRequest;
import gnu.project.backend.product.dto.response.StudioPageResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Studio API", description = "스튜디오 상품 관련 API")
public interface StudioDocs {

    @Operation(
        summary = "스튜디오 상품 생성",
        description = "스튜디오 상품을 생성하고 이미지를 업로드할 수 있습니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "스튜디오 상품 생성 성공",
                content = @Content(schema = @Schema(implementation = StudioResponse.class))
            )
        }
    )
    ResponseEntity<StudioResponse> createStudio(
        @Parameter(description = "스튜디오 상품 요청 DTO", required = true)
        @RequestPart("request") StudioRequest request,

        @Parameter(description = "업로드할 이미지 파일 목록", required = false)
        @RequestPart(value = "images", required = false) List<MultipartFile> images,

        @Auth Accessor accessor
    );

    @Operation(
        summary = "스튜디오 상품 수정",
        description = "기존 스튜디오 상품 정보를 수정하고 이미지를 변경할 수 있습니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "스튜디오 상품 수정 성공",
                content = @Content(schema = @Schema(implementation = StudioResponse.class))
            )
        }
    )
    ResponseEntity<StudioResponse> updateStudio(
        @Parameter(description = "수정할 상품 ID", required = true)
        @PathVariable(name = "id") Long id,

        @Parameter(description = "스튜디오 상품 수정 요청 DTO", required = true)
        @RequestPart("request") StudioUpdateRequest request,

        @Parameter(description = "업로드할 이미지 파일 목록", required = false)
        @RequestPart(value = "images", required = false) List<MultipartFile> images,

        @Auth Accessor accessor
    );

    @Operation(
        summary = "스튜디오 상품 단건 조회",
        description = "스튜디오 상품의 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = StudioResponse.class))
            )
        }
    )
    ResponseEntity<StudioResponse> readStudio(
        @Parameter(description = "조회할 상품 ID", required = true)
        @PathVariable(name = "id") Long id
    );

    @Operation(
        summary = "스튜디오 상품 목록 조회",
        description = "페이지네이션을 이용해 스튜디오 상품 목록을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = StudioPageResponse.class))
            )
        }
    )
    ResponseEntity<Page<StudioPageResponse>> readStudios(
        @Parameter(description = "페이지 번호 (기본값 1)")
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,

        @Parameter(description = "페이지 크기 (기본값 6)")
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") Integer pageSize
    );

    @Operation(
        summary = "스튜디오 상품 삭제",
        description = "특정 스튜디오 상품을 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공")
        }
    )
    ResponseEntity<String> deleteStudio(
        @Auth Accessor accessor,

        @Parameter(description = "삭제할 상품 ID", required = true)
        @PathVariable(name = "id") Long id
    );
}
