package gnu.project.backend.product.dto.response;

import gnu.project.backend.product.dto.response.DressResponse.TagResponse;
import java.time.LocalDateTime;
import java.util.List;

public record DressPageResponse(
    Long id,
    String name,
    String address,
    String detail,
    Integer price,
    String availableTime,
    LocalDateTime createdAt,
    String Thumbnail,
    List<TagResponse> tags
) {

}
