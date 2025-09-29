package gnu.project.backend.common.dto;

public record UploadImageDto(
    String key,
    String imageUrl
) {

    public static UploadImageDto of(String key, String imageUrl) {
        return new UploadImageDto(key, imageUrl);
    }
}