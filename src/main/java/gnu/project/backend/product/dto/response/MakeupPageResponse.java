package gnu.project.backend.product.dto.response;

import gnu.project.backend.product.enumerated.Region;
import java.time.LocalDateTime;

public record MakeupPageResponse(
    Long id,
    String name,
    String style,
    Double starCount,
    String address,
    String detail,
    Integer price,
    String type,
    String availableTime,
    LocalDateTime createdAt,
    Region region,
    
    String Thumbnail
) {

}
