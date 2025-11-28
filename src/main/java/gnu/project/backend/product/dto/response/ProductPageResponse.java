package gnu.project.backend.product.dto.response;

import gnu.project.backend.product.enumerated.Category;
import gnu.project.backend.product.enumerated.Region;
import java.time.LocalDateTime;
import java.util.List;

public record ProductPageResponse(
    Long id,
    String name,
    Double starCount,
    String address,
    String detail,
    Integer price,
    String availableTime,
    LocalDateTime createdAt,
    Region region,
    String thumbnail,
    Category category,
    String bzName,
    Integer averageRating,
    List<TagResponse> tags
) {

}
