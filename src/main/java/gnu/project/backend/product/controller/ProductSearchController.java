package gnu.project.backend.product.controller;

import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping
    public ResponseEntity<Page<ProductPageResponse>> searchAllProducts(
        @RequestParam String keyword,
        @RequestParam(required = false) Category category,
        @RequestParam(required = false, defaultValue = "LATEST") SortType sortType,
        @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
        @RequestParam(required = false, defaultValue = "6") Integer pageSize
    ) {
        return ResponseEntity.ok(
            productSearchService.search(keyword, category, sortType, pageNumber, pageSize)
        );
    }
}
