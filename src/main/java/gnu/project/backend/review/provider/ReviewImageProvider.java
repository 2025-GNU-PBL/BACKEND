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

    /**
     * 리뷰에서 업로드된 단일 이미지 파일을 S3에 저장하고 public URL을 리턴한다.
     * 이미지가 null/비어있으면 그냥 null 리턴.
     */
    public String uploadReviewImage(
            final Product product,
            final String customerSocialId,
            final MultipartFile image
    ) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        try {
            // WeddingHall에서는 category(ownerSocialId, ...) 기반으로 키를 만드는데
            // 리뷰는 REVIEW-<카테고리>/<고객소셜아이디>/... 형태로 정리
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
