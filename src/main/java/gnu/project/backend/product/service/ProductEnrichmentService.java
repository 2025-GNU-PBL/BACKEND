package gnu.project.backend.product.service;

import gnu.project.backend.product.dto.request.OptionCreateRequest;
import gnu.project.backend.product.dto.request.OptionUpdateRequest;
import gnu.project.backend.product.dto.request.TagRequest;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.provider.FileProvider;
import gnu.project.backend.product.provider.OptionProvider;
import gnu.project.backend.product.provider.TagProvider;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductEnrichmentService {

    private final FileProvider fileProvider;
    private final OptionProvider optionProvider;
    private final TagProvider tagProvider;

    /**
     * 상품 생성 시 이미지/옵션/태그 일괄 처리
     */
    @Transactional
    public void enrichNewProduct(
        Product product,
        List<MultipartFile> images,
        List<OptionCreateRequest> options,
        List<TagRequest> tags
    ) {
        processImages(product, images);
        processOptions(product, options);
        processTags(product, tags);
    }

    /**
     * 상품 수정 시 이미지/옵션/태그 일괄 처리
     */
    @Transactional
    public void updateProductEnrichment(
        Product product,
        List<MultipartFile> newImages,
        List<Long> keepImagesId,
        List<OptionUpdateRequest> options,
        List<TagRequest> tags
    ) {
        fileProvider.updateImages(product, newImages, keepImagesId, product.getImages());
        optionProvider.updateOptions(product, options);
        tagProvider.updateTags(product, tags);
    }

    private void processImages(Product product, List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            fileProvider.uploadAndSaveImages(product, images, new AtomicInteger(0));
        }
    }

    private void processOptions(Product product, List<OptionCreateRequest> options) {
        if (options != null && !options.isEmpty()) {
            optionProvider.createOptions(product, options);
        }
    }

    private void processTags(Product product, List<TagRequest> tags) {
        if (tags != null && !tags.isEmpty()) {
            tagProvider.createTag(product, tags);
        }
    }
}
