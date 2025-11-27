package gnu.project.backend.product.helper;

import gnu.project.backend.product.dto.request.OptionCreateRequest;
import gnu.project.backend.product.dto.request.OptionUpdateRequest;
import gnu.project.backend.product.dto.request.TagRequest;
import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.provider.ImageUploadProvider;
import gnu.project.backend.product.provider.OptionProvider;
import gnu.project.backend.product.provider.TagProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductHelper {

    private final ImageUploadProvider imageUploadProvider;
    private final OptionProvider optionProvider;
    private final TagProvider tagProvider;

    @Transactional
    public void createProduct(
        Product product,
        List<MultipartFile> images,
        List<OptionCreateRequest> options,
        List<TagRequest> tags
    ) {
        processImages(product, images);
        processOptions(product, options);
        processTags(product, tags);
    }

    @Transactional
    public void updateProductEnrichment(
        Product product,
        List<Image> existingImages,
        List<MultipartFile> newImages,
        List<Long> keepImagesId,
        List<OptionUpdateRequest> options,
        List<TagRequest> tags
    ) {
        imageUploadProvider.updateImages(product, newImages, keepImagesId, existingImages);
        optionProvider.updateOptions(product, options);
        tagProvider.updateTags(product, tags);
    }

    private void processImages(Product product, List<MultipartFile> images) {
        if (images != null && !images.isEmpty()) {
            imageUploadProvider.uploadAndSaveImages(product, images);
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
