package gnu.project.backend.product.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.aop.OnlyOwner;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.controller.docs.ProductDocs;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController implements ProductDocs {

    private final ProductService productService;

    @OnlyOwner
    @GetMapping()
    public ResponseEntity<Page<ProductPageResponse>> getMyProducts(
        @Parameter(hidden = true) @Auth final Accessor accessor,
        @RequestParam(required = false, defaultValue = "1") final Integer pageNumber,
        @RequestParam(required = false, defaultValue = "6") final Integer pageSize
    ) {
        return ResponseEntity.ok(productService.getMyProducts(accessor, pageNumber, pageSize));
    }
}
