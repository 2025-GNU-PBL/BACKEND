package gnu.project.backend.product.provider;

import static gnu.project.backend.common.error.ErrorCode.IMAGE_UPLOAD_FAILED;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.common.service.FileService;
import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ImageRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class fileProvider {

    private final FileService fileService;
    private final ImageRepository imageRepository;
    private final Executor imageUploadExecutor = Executors.newFixedThreadPool(10);


    public void uploadAndSaveImages(
        final Product product,
        final List<MultipartFile> images,
        AtomicInteger sequence) {
        if (images == null || images.isEmpty()) {
            return;
        }
        uploadImages(product, images, sequence);
    }

    private void uploadImages(Product product, List<MultipartFile> images, AtomicInteger sequence) {
        final List<CompletableFuture<Image>> futures = images.stream()
            .map(image -> CompletableFuture.supplyAsync(() -> {
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
                            sequence.getAndIncrement()
                        );
                    }, imageUploadExecutor)
                    .exceptionally(ex -> {
                        throw new BusinessException(IMAGE_UPLOAD_FAILED);
                    })
            )
            .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        final List<Image> imageEntities = allOf
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList()
            )
            .join();

        imageRepository.saveAll(imageEntities);
        imageEntities.forEach(product::addImage);
    }

    public void updateImages(
        final Product product,
        final List<MultipartFile> newImages,
        final List<Long> keepImageIds,
        final List<Image> existingImages
    ) {
        List<Image> imagesToDelete = existingImages.stream()
            .filter(image -> keepImageIds == null || !keepImageIds.contains(image.getId()))
            .toList();

        imagesToDelete.forEach(image -> fileService.delete(image.getS3Key()));
        imageRepository.deleteAll(imagesToDelete);
        product.getImages().removeAll(imagesToDelete);

        if (newImages != null && !newImages.isEmpty()) {
            final AtomicInteger sequence = new AtomicInteger(
                (int) existingImages.stream()
                    .filter(image -> keepImageIds != null && keepImageIds.contains(image.getId()))
                    .count()
            );

            uploadAndSaveImages(product, newImages, sequence);
        }
    }
}
