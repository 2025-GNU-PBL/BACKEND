package gnu.project.backend.common.service;

import static gnu.project.backend.common.constant.S3GenerateKeyConstant.FILENAME_SEPARATOR;
import static gnu.project.backend.common.constant.S3GenerateKeyConstant.FOLDER_SEPARATOR;
import static gnu.project.backend.common.error.ErrorCode.IMAGE_DELETE_FAILED;
import static gnu.project.backend.common.error.ErrorCode.IMAGE_DOWNLOAD_FAILED;
import static gnu.project.backend.common.error.ErrorCode.IMAGE_FILE_INVALID_NAME;
import static gnu.project.backend.common.error.ErrorCode.IMAGE_FILE_READ_FAILED;
import static gnu.project.backend.common.error.ErrorCode.IMAGE_INVALID_FORMAT;
import static gnu.project.backend.common.error.ErrorCode.IMAGE_UPLOAD_FAILED;

import com.monari.monariback.common.enumerated.ImageExtension;
import gnu.project.backend.common.dto.DownloadImageDto;
import gnu.project.backend.common.dto.UploadImageDto;
import gnu.project.backend.common.exception.BusinessException;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {


    private final S3Client s3Client;


    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public void uploadFile(String key, byte[] data, String contentType) {
        try {
            final PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();
            s3Client.putObject(putReq, RequestBody.fromBytes(data));
        } catch (SdkException e) {
            log.error(e.getMessage());
            throw new BusinessException(IMAGE_UPLOAD_FAILED);
        }
    }

    public void delete(final String s3Key) {
        try {
            s3Client.deleteObject(builder -> builder
                .bucket(bucketName)
                .key(s3Key)
            );
        } catch (SdkException e) {
            throw new BusinessException(IMAGE_DELETE_FAILED);
        }
    }

    public String generateImageUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    public UploadImageDto uploadImageWithUrl(
        final String folder,
        final String socialId,
        final MultipartFile file
    ) {
        validateImageFile(file);
        final String key = generateKey(folder, socialId, file.getOriginalFilename());
        try {
            final byte[] data = file.getBytes();
            final String contentType = file.getContentType();
            uploadFile(key, data, contentType);

            final String imageUrl = generateImageUrl(key);

            return new UploadImageDto(key, imageUrl);
        } catch (IOException e) {
            throw new BusinessException(IMAGE_FILE_READ_FAILED);
        }
    }

    public String uploadImage(
        final String folder,
        final String socialId,
        final MultipartFile file
    ) {
        validateImageFile(file);
        final String key = generateKey(folder, socialId, file.getOriginalFilename());
        try {
            final byte[] data = file.getBytes();
            final String contentType = file.getContentType();
            uploadFile(key, data, contentType);
            return key;
        } catch (IOException e) {
            throw new BusinessException(IMAGE_FILE_READ_FAILED);
        }
    }


    public DownloadImageDto downloadFile(String key) {
        try {
            ResponseBytes<GetObjectResponse> respBytes =
                s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            MediaType mediaType = deriveMediaTypeFromKey(key);
            return DownloadImageDto.from(
                respBytes.asByteArray(), mediaType);
        } catch (SdkException e) {
            throw new BusinessException(IMAGE_DOWNLOAD_FAILED);
        }
    }

    private void validateImageFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new BusinessException(IMAGE_FILE_INVALID_NAME);
        }

        String ext = extractExtension(filename);
        if (!ImageExtension.isSupported(ext)) {
            throw new BusinessException(IMAGE_INVALID_FORMAT);
        }
    }

    private void validateImageFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename.isBlank()) {
            throw new BusinessException(IMAGE_FILE_INVALID_NAME);
        }

        String ext = extractExtension(originalFilename);
        if (!ImageExtension.isSupported(ext)) {
            throw new BusinessException(IMAGE_INVALID_FORMAT);
        }
    }

    private MediaType deriveMediaTypeFromKey(String key) {
        String ext = extractExtension(key);
        return ImageExtension.mediaTypeFor(ext);
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            throw new BusinessException(IMAGE_INVALID_FORMAT);
        }
        return filename.substring(idx + 1).toLowerCase();
    }

    private String generateKey(String folder, String socialId, String originalFilename) {
        final String extension = extractExtension(originalFilename);
        final String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        final String uniqueName =
            baseName + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
        return folder + FOLDER_SEPARATOR + socialId + FILENAME_SEPARATOR + uniqueName;
    }
}
