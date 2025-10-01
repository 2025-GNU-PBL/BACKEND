package gnu.project.backend.review.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {
    private String title;
    @NotNull
    private Short star;
    private String comment;
    private String imageUrl;
}
