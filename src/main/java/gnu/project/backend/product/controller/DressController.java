package gnu.project.backend.product.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.DressRequest;
import gnu.project.backend.product.dto.request.MakeupUpdateRequest;
import gnu.project.backend.product.dto.response.DressPageResponse;
import gnu.project.backend.product.dto.response.DressResponse;
import gnu.project.backend.product.service.DressService;
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
@RequestMapping("/api/v1/dress")
public class DressController {

    private final DressService dressService;

    @PostMapping()
    public ResponseEntity<DressResponse> createMakeup(
        @Valid @RequestPart("request") final DressRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                dressService.create(
                    request, images, accessor
                )
            );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DressResponse> updateDress(
        @PathVariable(name = "id") final Long id,
        @Valid @RequestPart("request") final MakeupUpdateRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                dressService.update(
                    id, request, images, accessor, request.keepImagesId()
                )
            );
    }


    @GetMapping("/{id}")
    public ResponseEntity<DressResponse> readMakeup(
        @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(dressService.read(id));
    }

    @GetMapping()
    public ResponseEntity<Page<DressPageResponse>> readMakeups(
        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
        @RequestParam(name = "pageSize", required = false, defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(
            dressService.readDresses(pageNumber, pageSize)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDress(
        @Auth final Accessor accessor,
        @PathVariable(name = "id") final Long id
    ) {
        return ResponseEntity.ok(dressService.delete(id, accessor));
    }


}
