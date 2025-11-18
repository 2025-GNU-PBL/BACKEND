package gnu.project.backend.product.controller;

import static gnu.project.backend.product.constant.PageConstant.DEFAULT_PAGE_NUMBER;
import static gnu.project.backend.product.constant.PageConstant.DEFAULT_PAGE_SIZE;
import static gnu.project.backend.product.constant.ProductConstant.DEFAULT_SORT_TYPE;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.controller.docs.ProductSearchDocs;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.dto.response.RecentSearchResponse;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class ProductSearchController implements ProductSearchDocs {

    private final ProductSearchService productSearchService;

    @GetMapping
    public ResponseEntity<Page<ProductPageResponse>> searchAllProducts(
        @RequestParam final String keyword,
        @RequestParam(required = false, defaultValue = DEFAULT_SORT_TYPE) SortType sortType,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_NUMBER) Integer pageNumber,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize
    ) {
        return ResponseEntity.ok(
            productSearchService.search(keyword, sortType, pageNumber, pageSize)
        );
    }

    @PostMapping("/recent")
    public ResponseEntity<RecentSearchResponse> saveSearchKeyword(
        @RequestParam final String keyword,
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(productSearchService.saveSearchKeyword(accessor, keyword));
    }

    @GetMapping("/recent")
    public ResponseEntity<RecentSearchResponse> getRecentSearches(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(productSearchService.getRecentSearches(accessor));
    }

    @DeleteMapping("/recent")
    public ResponseEntity<RecentSearchResponse> deleteSearchKeyword(
        @RequestParam final String keyword,
        @Auth final Accessor accessor

    ) {
        return ResponseEntity.ok(productSearchService.deleteSearchKeyword(accessor, keyword));
    }

    @DeleteMapping("/recent/all")
    public ResponseEntity<RecentSearchResponse> deleteAllSearches(
        @Auth final Accessor accessor
    ) {
        return ResponseEntity.ok(productSearchService.deleteAllSearches(accessor));
    }
}
