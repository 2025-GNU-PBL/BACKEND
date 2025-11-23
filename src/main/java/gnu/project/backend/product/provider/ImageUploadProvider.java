package gnu.project.backend.product.provider;

import static gnu.project.backend.common.error.ErrorCode.IMAGE_UPLOAD_FAILED;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.common.service.FileService;
import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ImageRepository;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUploadProvider {

    private final FileService fileService;
    private final ImageRepository imageRepository;
    private final AsyncFileUploader asyncFileUploader;

    @Qualifier("fileUploadExecutor")
    private final Executor uploadExecutor;

    public void uploadAndSaveImages(
        final Product product,
        final List<MultipartFile> images
    ) {
        if (!hasFiles(images)) {
            return;
        }

        int startSequence = product.getImages().size();
        uploadImagesWithSequence(product, images, startSequence);
    }

    public void updateImages(
        final Product product,
        final List<MultipartFile> newImages,
        final List<Long> keepImageIds,
        final List<Image> existingImages
    ) {
        List<Image> imagesToDelete = filterImagesToDelete(existingImages, keepImageIds);

        if (!imagesToDelete.isEmpty()) {
            imageRepository.deleteAll(imagesToDelete);
            product.getImages().removeAll(imagesToDelete);
        }

        List<Image> remainingImages = existingImages.stream()
            .filter(image -> !imagesToDelete.contains(image))
            .sorted(Comparator.comparing(Image::getDisplayOrder))
            .toList();

        for (int i = 0; i < remainingImages.size(); i++) {
            remainingImages.get(i).updateDisplayOrder(i);
        }

        if (hasFiles(newImages)) {
            int startSequence = remainingImages.size();
            uploadImagesWithSequence(product, newImages, startSequence);
        }

        deleteImagesFromS3(imagesToDelete);
    }

    private void uploadImagesWithSequence(
        final Product product,
        final List<MultipartFile> images,
        final int startSequence
    ) {
        AtomicInteger sequence = new AtomicInteger(startSequence);

        final List<CompletableFuture<Image>> futures = images.stream()
            .map(image -> createImageUploadFuture(product, image, sequence))
            .toList();

        final List<Image> imageEntities = asyncFileUploader.executeAsyncUploads(futures);

        imageRepository.saveAll(imageEntities);
        imageEntities.forEach(product::addImage);
    }

    private CompletableFuture<Image> createImageUploadFuture(
        final Product product,
        final MultipartFile image,
        final AtomicInteger sequence
    ) {
        final int assignedSequence = sequence.getAndIncrement();
        return CompletableFuture.supplyAsync(() -> {
            String fileName = image.getOriginalFilename();
            try {
                String key = fileService.uploadImage(
                    product.getCategory().toString(),
                    product.getOwner().getSocialId(),
                    image
                );
                String url = fileService.generateImageUrl(key);

                return Image.ofCreate(
                    product,
                    url,
                    key,
                    assignedSequence
                );
            } catch (Exception ex) {
                log.error("Failed to upload image: {}, product: {}, error: {}",
                    fileName, product.getId(), ex.getMessage(), ex);
                throw new BusinessException(
                    IMAGE_UPLOAD_FAILED
                );
            }
        }, uploadExecutor);
    }

    private void deleteImagesFromS3(final List<Image> images) {
        images.forEach(image -> {
            try {
                fileService.delete(image.getS3Key());
                log.debug("Successfully deleted S3 image: {}", image.getS3Key());
            } catch (Exception e) {
                log.warn("Failed to delete S3 object. s3Key={}, productId={}",
                    image.getS3Key(), image.getProduct().getId(), e);
            }
        });
    }

    private List<Image> filterImagesToDelete(
        final List<Image> existingImages,
        final List<Long> keepImageIds
    ) {
        if (keepImageIds == null) {
            return existingImages;
        }

        return existingImages.stream()
            .filter(image -> !keepImageIds.contains(image.getId()))
            .toList();
    }

    private int calculateStartSequence(
        final List<Image> existingImages,
        final List<Long> keepImageIds
    ) {
        if (keepImageIds == null) {
            return 0;
        }

        return (int) existingImages.stream()
            .filter(image -> keepImageIds.contains(image.getId()))
            .count();
    }

    private boolean hasFiles(final List<MultipartFile> files) {
        return files != null && !files.isEmpty();
    }
}