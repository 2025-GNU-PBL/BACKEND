package gnu.project.backend.product.dto.request;

import gnu.project.backend.product.constant.MakeupConstant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MakeupUpdateRequest(

    @NotBlank(message = MakeupConstant.NAME_REQUIRED)
    @Size(max = MakeupConstant.MAX_NAME_LENGTH, message = MakeupConstant.NAME_LENGTH)
    String name,

    @NotNull(message = MakeupConstant.PRICE_REQUIRED)
    @Min(value = MakeupConstant.MIN_PRICE, message = MakeupConstant.PRICE_MIN)
    Integer price,

    @NotBlank(message = MakeupConstant.ADDRESS_REQUIRED)
    @Size(max = MakeupConstant.MAX_ADDRESS_LENGTH, message = MakeupConstant.ADDRESS_LENGTH)
    String address,

    @NotBlank(message = MakeupConstant.DETAIL_REQUIRED)
    @Size(max = MakeupConstant.MAX_DETAIL_LENGTH, message = MakeupConstant.DETAIL_LENGTH)
    String detail,

    @NotBlank(message = MakeupConstant.STYLE_REQUIRED)
    @Size(max = MakeupConstant.MAX_STYLE_LENGTH, message = MakeupConstant.STYLE_LENGTH)
    String style,

    @Size(max = MakeupConstant.MAX_AVAILABLE_TIMES, message = MakeupConstant.AVAILABLE_TIMES_LENGTH)
    String availableTimes,

    @NotBlank(message = MakeupConstant.TYPE_REQUIRED)
    @Size(max = MakeupConstant.MAX_TYPE_LENGTH, message = MakeupConstant.TYPE_LENGTH)
    String type,

    @Valid
    @Size(max = MakeupConstant.MAX_OPTION_COUNT, message = MakeupConstant.MAKEUP_OPTION_LIMIT)
    List<OptionCreateRequest> options,

    List<Long> keepImagesId
) {

}
