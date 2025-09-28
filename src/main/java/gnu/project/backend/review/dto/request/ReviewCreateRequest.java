package gnu.project.backend.review.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {
    private String title;
    private Short star;
    private String comment;
    private String imageUrl;
}
