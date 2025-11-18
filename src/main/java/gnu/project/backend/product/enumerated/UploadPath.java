package gnu.project.backend.product.enumerated;

public enum UploadPath {
    SCHEDULE("SCHEDULE"),
    DRESS("DRESS"),
    STUDIO("STUDIO"),
    MAKEUP("MAKEUP");

    private final String path;

    UploadPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
