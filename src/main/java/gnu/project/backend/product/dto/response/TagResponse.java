package gnu.project.backend.product.dto.response;

import gnu.project.backend.product.entity.Tag;

public record TagResponse(
    Long id,
    String tagName
) {

    public static TagResponse from(Tag tag) {
        return new TagResponse(
            tag.getId(),
            tag.getName()
        );
    }
}
