package gnu.project.backend.product.provider;

import static gnu.project.backend.common.error.ErrorCode.FILE_READ_FAILED;
import static gnu.project.backend.common.error.ErrorCode.FILE_UPLOAD_FAILED;
import static gnu.project.backend.common.error.ErrorCode.IMAGE_UPLOAD_FAILED;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.common.service.FileService;
import gnu.project.backend.product.entity.Image;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ImageRepository;
import gnu.project.backend.schedule.entity.Schedule;
import gnu.project.backend.schedule.entity.ScheduleFile;
import gnu.project.backend.schedule.repository.ScheduleFileRepository;
import java.io.IOException;
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
public class FileProvider {

    private final FileService fileService;
    private final ImageRepository imageRepository;
    private final ScheduleFileRepository scheduleFileRepository;

    @Qualifier("fileUploadExecutor")
    private final Executor uploadExecutor;
    // TODO : 향후 enum 으로 관리
    private final String schedulePath = "SCHEDULE";

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
                    }, uploadExecutor)
                    .exceptionally(ex -> {
                        log.error("File upload failed", ex);
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

        imagesToDelete.forEach(image -> {
            try {
                fileService.delete(image.getS3Key());
            } catch (Exception e) {
                log.warn("Failed to delete S3 object. s3Key={}", image.getS3Key(), e);
            }
        });

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

    public void uploadAndSaveFiles(final Schedule schedule, final List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        final List<CompletableFuture<ScheduleFile>> futures = files.stream()
            .map(file -> {
                try {
                    byte[] fileBytes = file.getBytes();
                    return CompletableFuture.supplyAsync(() -> {
                        String key = fileService.uploadDocument(
                            schedulePath,
                            fileBytes,
                            file
                        );
                        return ScheduleFile.ofCreate(schedule, key, file);
                    }, uploadExecutor).exceptionally(ex -> {
                        log.error("File upload failed", ex);
                        throw new BusinessException(FILE_UPLOAD_FAILED);
                    });
                } catch (IOException e) {
                    log.error("File read failed", e);
                    throw new BusinessException(FILE_READ_FAILED);
                }
            })
            .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        final List<ScheduleFile> scheduleFiles = allOf.thenApply(v ->
            futures.stream().map(CompletableFuture::join).toList()
        ).join();

        scheduleFileRepository.saveAll(scheduleFiles);
        scheduleFiles.forEach(schedule::addFiles);
    }
}
