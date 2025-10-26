package gnu.project.backend.product.dto.response;

import gnu.project.backend.product.enurmerated.Region;

import java.time.LocalDateTime;
import java.util.List;


public record WeddingHallPageResponse(
        Long id,
        String name,
        Double starCount,
        String address,
        String detail,
        Integer price,
        Integer hallType,
        String availableTime,
        LocalDateTime createdAt,
        String thumbnail,
        Region region,
        List<WeddingHallResponse.TagResponse> tags
) {
}
