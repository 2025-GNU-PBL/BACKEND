package gnu.project.backend.product.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.WeddingHallRequest;
import gnu.project.backend.product.dto.request.WeddingHallUpdateRequest;
import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.enurmerated.Region;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(
        name = "WeddingHall API",
        description = "웨딩홀 상품 CRUD 및 마이페이지 전용 조회 API"
)
public interface WeddingHallDocs {

    @Operation(
            summary = "웨딩홀 생성",
            description = "오너가 새 웨딩홀 상품을 등록한다. request(JSON) + images(files[]) 멀티파트 업로드.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "생성 성공",
                            content = @Content(schema = @Schema(implementation = WeddingHallResponse.class))
                    )
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<WeddingHallResponse> createWeddingHall(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestPart("request") WeddingHallRequest request,
            @RequestPart(name = "images", required = false) List<MultipartFile> images
    );

    @Operation(
            summary = "웨딩홀 수정",
            description = "기존 웨딩홀 수정. 새 이미지 업로드 / 기존 이미지 유지 / 옵션, 태그 갱신 등.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(schema = @Schema(implementation = WeddingHallResponse.class))
                    )
            }
    )
    @PatchMapping(value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<WeddingHallResponse> updateWeddingHall(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable("id") Long id,
            @RequestPart("request") WeddingHallUpdateRequest request,
            @RequestPart(name = "images", required = false) List<MultipartFile> images
    );

    @Operation(
            summary = "웨딩홀 삭제",
            description = "소프트 삭제. isDeleted=true 로만 바뀌고 실제로는 안 지움.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "삭제 성공",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteWeddingHall(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable("id") Long id
    );

    @Operation(
            summary = "웨딩홀 상세 조회",
            description = "단일 웨딩홀 상세. 이미지, 옵션, 태그 등 전부 포함.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = WeddingHallResponse.class))
                    )
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<WeddingHallResponse> readWeddingHall(
            @PathVariable("id") Long id
    );

    @Operation(
            summary = "웨딩홀 전체 목록 조회",
            description = "공개 리스트 조회. 페이징으로 카드 뿌릴 때 사용.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = Page.class))
                    )
            }
    )
    @GetMapping
    ResponseEntity<Page<WeddingHallPageResponse>> readWeddingHalls(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "6") Integer pageSize,
            @RequestParam(name = "region", required = false) final Region region
    );

    @Operation(
            summary = "내 웨딩홀 목록 조회 (오너 전용)",
            description = "로그인한 오너(Accessor)의 웨딩홀들만 페이징해서 가져온다. 마이페이지용.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = Page.class))
                    )
            }
    )
    @GetMapping("/me")
    ResponseEntity<Page<WeddingHallPageResponse>> readMyWeddingHalls(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "6") Integer pageSize
    );
}
