package gnu.project.backend.product.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.WeddingHallRequest;
import gnu.project.backend.product.dto.request.WeddingHallUpdateRequest;
import gnu.project.backend.product.dto.response.WeddingHallPageResponse;
import gnu.project.backend.product.dto.response.WeddingHallResponse;
import gnu.project.backend.product.enumerated.Region;
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

@Tag(name = "WeddingHall API", description = "웨딩홀 상품 CRUD 및 조회 API")
public interface WeddingHallDocs {

    @Operation(summary = "웨딩홀 생성", description = "오너 등록. request(JSON) + images(files[]) 멀티파트 업로드.")
    @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = WeddingHallResponse.class)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<WeddingHallResponse> createWeddingHall(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestPart("request") WeddingHallRequest request,
            @RequestPart(name = "images", required = false) List<MultipartFile> images
    );

    @Operation(summary = "웨딩홀 수정", description = "이미지/옵션/태그/기본정보 갱신")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = WeddingHallResponse.class)))
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<WeddingHallResponse> updateWeddingHall(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable("id") Long id,
            @RequestPart("request") WeddingHallUpdateRequest request,
            @RequestPart(name = "images", required = false) List<MultipartFile> images
    );

    @Operation(summary = "웨딩홀 삭제", description = "소프트 삭제(isDeleted=true)")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteWeddingHall(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable("id") Long id
    );

    @Operation(summary = "웨딩홀 상세 조회", description = "이미지/옵션/태그 포함 상세")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = WeddingHallResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<WeddingHallResponse> readWeddingHall(@PathVariable("id") Long id);

    @Operation(summary = "웨딩홀 목록 조회",
            description = "공개 리스트 조회. 최신순(updatedAt DESC). region/subway/dining 필터 지원")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @GetMapping
    ResponseEntity<Page<WeddingHallPageResponse>> readWeddingHalls(
            @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize,
            @RequestParam(name = "region", required = false) Region region,
            @RequestParam(name = "subwayAccessible", required = false) Boolean subwayAccessible,
            @RequestParam(name = "diningAvailable", required = false) Boolean diningAvailable
    );

    @Operation(summary = "내 웨딩홀 목록", description = "오너 본인 소유 상품")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @GetMapping("/me")
    ResponseEntity<Page<WeddingHallPageResponse>> readMyWeddingHalls(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "6") Integer pageSize
    );
}
