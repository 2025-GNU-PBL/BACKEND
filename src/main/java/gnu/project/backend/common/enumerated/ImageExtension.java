package gnu.project.backend.common.enumerated;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import org.springframework.http.MediaType;

public enum ImageExtension {
    PNG("png", MediaType.IMAGE_PNG),
    JPG("jpg", MediaType.IMAGE_JPEG),
    JPEG("jpeg", MediaType.IMAGE_JPEG),
    GIF("gif", MediaType.IMAGE_GIF),
    BMP("bmp", MediaType.parseMediaType("image/bmp")),
    WEBP("webp", MediaType.parseMediaType("image/webp"));

    private final String extension;

    @Getter
    private final MediaType mediaType;

    ImageExtension(String extension, MediaType mediaType) {
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public static boolean isSupported(String ext) {
        if (ext == null) {
            return false;
        }
        return Arrays.stream(values())
            .anyMatch(e -> e.extension.equalsIgnoreCase(ext));
    }

    public static MediaType mediaTypeFor(String ext) {
        return from(ext)
            .map(ImageExtension::getMediaType)
            .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    private static Optional<ImageExtension> from(String ext) {
        if (ext == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
            .filter(e -> e.extension.equalsIgnoreCase(ext))
            .findFirst();
    }
}
