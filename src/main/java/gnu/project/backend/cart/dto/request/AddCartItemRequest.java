package gnu.project.backend.cart.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AddCartItemRequest {
    private Long productId;
    private Long optionId;
    private LocalDateTime desireDate;
    private Integer quantity;
    private String memo;
}
