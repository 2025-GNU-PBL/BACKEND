package gnu.project.backend.product.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.MakeupRequest;
import gnu.project.backend.product.dto.request.MakeupUpdateRequest;
import gnu.project.backend.product.dto.response.MakeupPageResponse;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.service.MakeupService;
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
public class MakeupController {

    private final MakeupService makeupService;

    @PostMapping()
    public ResponseEntity<MakeupResponse> createMakeup(
        @Valid @RequestPart("request") final MakeupRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                makeupService.create(
                    request, images, accessor
                )
            );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MakeupResponse> updateMakeup(
        @PathVariable(name = "id") final Long id,
        @Valid @RequestPart("request") final MakeupUpdateRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                makeupService.update(
                    id, request, images, accessor, request.keepImagesId()
                )
            );
    }


    @GetMapping("/{id}")
    public ResponseEntity<MakeupResponse> readMakeup(
        @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(makeupService.read(id));
    }

    @GetMapping()
    public ResponseEntity<Page<MakeupPageResponse>> readMakeups(
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(
            makeupService.readMakeups(pageNumber, pageSize)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMakeup(
        @Auth final Accessor accessor,
        @RequestParam(name = "id") final Long id
    ) {
        return ResponseEntity.ok(makeupService.delete(id, accessor));
    }

}
