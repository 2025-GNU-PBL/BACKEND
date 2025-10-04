package gnu.project.backend.review.dto.request;

import lombok.Getter;

@Getter
public class ReviewUpdateRequest {
    private String comment;
    private Short star;
    private String imageUrl;
}
