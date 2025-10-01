package gnu.project.backend.product.controller;


import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.request.MakeupCreateRequest;
import gnu.project.backend.product.dto.request.StudioCreateRequest;
import gnu.project.backend.product.dto.response.MakeupResponse;
import gnu.project.backend.product.dto.response.StudioResponse;
import gnu.project.backend.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/makeup")
    public ResponseEntity<MakeupResponse> createMakeup(
        @Valid @RequestPart("request") final MakeupCreateRequest request,
        @RequestPart(value = "images", required = false) final List<MultipartFile> images,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                productService.createMakeup(
                    request, images, accessor
                )
            );
    }

    @GetMapping("/makeup/{makeupId}")
    public ResponseEntity<MakeupResponse> createMakeup(
        @PathVariable(name = "makeupId") final Long makeupId
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                productService.findMakeup(
                    makeupId
                )
            );
    }


    @PostMapping("/studio")
    public ResponseEntity<StudioResponse> createStudio(
        @Valid @RequestBody StudioCreateRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                productService.createStudio(request)
            );
    }
}
