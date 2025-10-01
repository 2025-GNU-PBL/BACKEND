package gnu.project.backend.common.dto;

import org.springframework.http.MediaType;

public record DownloadImageDto(
    byte[] data,
    MediaType contentType
) {

    public static DownloadImageDto from(byte[] data, MediaType contentType) {
        return new DownloadImageDto(data, contentType);
    }
}
