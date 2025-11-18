package gnu.project.backend.product.provider;

import static gnu.project.backend.common.error.ErrorCode.FILE_READ_FAILED;
import static gnu.project.backend.common.error.ErrorCode.FILE_UPLOAD_FAILED;

import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.common.service.FileService;
import gnu.project.backend.product.enumerated.UploadPath;
import gnu.project.backend.schedule.entity.Schedule;
import gnu.project.backend.schedule.entity.ScheduleFile;
import gnu.project.backend.schedule.repository.ScheduleFileRepository;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleFileProvider {

    private final FileService fileService;
    private final ScheduleFileRepository scheduleFileRepository;
    private final AsyncFileUploader asyncFileUploader;

    @Qualifier("fileUploadExecutor")
    private final Executor uploadExecutor;

    public void uploadAndSaveFiles(
        final Schedule schedule,
        final List<MultipartFile> files
    ) {
        if (!hasFiles(files)) {
            return;
        }

        final List<CompletableFuture<ScheduleFile>> futures = files.stream()
            .map(file -> createFileUploadFuture(schedule, file))
            .toList();

        final List<ScheduleFile> scheduleFiles = asyncFileUploader.executeAsyncUploads(futures);

        scheduleFileRepository.saveAll(scheduleFiles);
        scheduleFiles.forEach(schedule::addFiles);
    }

    public void updateScheduleFiles(
        final Schedule schedule,
        final List<MultipartFile> files,
        final List<Long> keepFileIds
    ) {
        final List<ScheduleFile> existingFiles = schedule.getFiles();

        if (hasFiles(files)) {
            uploadAndSaveFiles(schedule, files);
        }

        final List<ScheduleFile> filesToDelete = filterFilesToDelete(existingFiles, keepFileIds);

        if (!filesToDelete.isEmpty()) {
            scheduleFileRepository.deleteAll(filesToDelete);
            existingFiles.removeAll(filesToDelete);
        }

        deleteFilesFromS3(filesToDelete);
    }

    public void deleteFiles(final List<ScheduleFile> files) {
        if (files.isEmpty()) {
            return;
        }

        scheduleFileRepository.deleteAll(files);
        deleteFilesFromS3(files);
    }

    private CompletableFuture<ScheduleFile> createFileUploadFuture(
        final Schedule schedule,
        final MultipartFile file
    ) {
        final String fileName = file.getOriginalFilename();

        try {
            byte[] fileBytes = file.getBytes();

            return CompletableFuture.supplyAsync(() -> {
                try {
                    String key = fileService.uploadDocument(
                        UploadPath.SCHEDULE.getPath(),
                        fileBytes,
                        file
                    );
                    log.debug("Successfully uploaded file: {}", fileName);
                    return ScheduleFile.ofCreate(schedule, key, file);
                } catch (Exception ex) {
                    log.error("Failed to upload file: {}, schedule: {}, error: {}",
                        fileName, schedule.getId(), ex.getMessage(), ex);
                    throw new BusinessException(
                        FILE_UPLOAD_FAILED
                    );
                }
            }, uploadExecutor);

        } catch (IOException e) {
            log.error("Failed to read file: {}, error: {}", fileName, e.getMessage(), e);
            throw new BusinessException(
                FILE_READ_FAILED
            );
        }
    }

    private void deleteFilesFromS3(final List<ScheduleFile> files) {
        files.forEach(file -> {
            try {
                fileService.delete(file.getFilePath());
                log.debug("Successfully deleted S3 file: {}", file.getFilePath());
            } catch (Exception e) {
                log.warn("Failed to delete S3 file: {}, scheduleId: {}",
                    file.getFilePath(), file.getSchedule().getId(), e);
            }
        });
    }

    private List<ScheduleFile> filterFilesToDelete(
        final List<ScheduleFile> existingFiles,
        final List<Long> keepFileIds
    ) {
        if (keepFileIds == null) {
            return existingFiles;
        }

        return existingFiles.stream()
            .filter(file -> !keepFileIds.contains(file.getId()))
            .toList();
    }

    private boolean hasFiles(final List<MultipartFile> files) {
        return files != null && !files.isEmpty();
    }
}