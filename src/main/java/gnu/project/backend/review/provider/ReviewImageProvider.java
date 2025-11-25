package gnu.project.backend.review.provider;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.common.service.ImageService;
import gnu.project.backend.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static gnu.project.backend.common.error.ErrorCode.IMAGE_UPLOAD_FAILED;

@Component
@RequiredArgsConstructor
public class ReviewImageProvider {

    private final ImageService imageService;

    public List<String> uploadReviewImages(
            final Product product,
            final String customerSocialId,
            final List<MultipartFile> images
    ) {
        if (images == null || images.isEmpty()) return List.of();

        List<String> result = new ArrayList<>();

        for (MultipartFile image : images) {
            try {
                String key = imageService.uploadImage(
                        "REVIEW-" + product.getCategory(),
                        customerSocialId,
                        image
                );

                result.add(imageService.generateImageUrl(key));

            } catch (Exception e) {
                throw new BusinessException(IMAGE_UPLOAD_FAILED);
            }
        }

        return result;
    }
}
