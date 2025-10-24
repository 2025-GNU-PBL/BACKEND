package gnu.project.backend.common.enumerated;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import org.springframework.http.MediaType;

public enum FileExtension {
    PDF("pdf", MediaType.APPLICATION_PDF);

    private final String extension;

    @Getter
    private final MediaType mediaType;

    FileExtension(String extension, MediaType mediaType) {
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
            .map(FileExtension::getMediaType)
            .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    private static Optional<FileExtension> from(String ext) {
        if (ext == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
            .filter(e -> e.extension.equalsIgnoreCase(ext))
            .findFirst();
    }

}
