package gnu.project.backend.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MakeupRequest(

    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 100, message = "상품명은 100자 이하여야 합니다")
    String name,

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    Integer price,

    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 500, message = "주소는 500자 이하여야 합니다")
    String address,

    @NotBlank(message = "상세 설명은 필수입니다")
    @Size(max = 2000, message = "상세 설명은 2000자 이하여야 합니다")
    String detail,

    @NotBlank(message = "메이크업 스타일은 필수입니다")
    @Size(max = 50, message = "스타일은 50자 이하여야 합니다")
    String style,

    @Size(max = 100, message = "이용 가능 시간은 100자 이하여야 합니다")
    String availableTimes,

    @NotBlank(message = "메이크업 타입은 필수입니다")
    @Size(max = 50, message = "타입은 50자 이하여야 합니다")
    String type,

    @Valid
    @Size(max = 10, message = "옵션은 최대 10개까지 추가 가능합니다")
    List<OptionCreateRequest> options
) {

}
