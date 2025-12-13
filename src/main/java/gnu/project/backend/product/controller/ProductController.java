package gnu.project.backend.product.controller;

import static gnu.project.backend.product.constant.PageConstant.DEFAULT_PAGE_NUMBER;
import static gnu.project.backend.product.constant.PageConstant.DEFAULT_PAGE_SIZE;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.aop.OnlyOwner;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.controller.docs.ProductDocs;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
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
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer pageNumber,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize
    ) {
        return ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .header("Pragma", "no-cache")
            .header("Expires", "0")
            .body(productService.getMyProducts(accessor, pageNumber, pageSize));
    }
}
