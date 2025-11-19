package gnu.project.backend.review.provider;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.common.service.ImageService;
import gnu.project.backend.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static gnu.project.backend.common.error.ErrorCode.IMAGE_UPLOAD_FAILED;

@Component
@RequiredArgsConstructor
public class ReviewImageProvider {

    private final ImageService imageService;

    public String uploadReviewImage(
            final Product product,
            final String customerSocialId,
            final MultipartFile image
    ) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        try {
            final String key = imageService.uploadImage(
                    "REVIEW-" + product.getCategory().toString(),
                    customerSocialId,
                    image
            );

            return imageService.generateImageUrl(key);

        } catch (Exception e) {
            throw new BusinessException(IMAGE_UPLOAD_FAILED);
        }
    }
}
