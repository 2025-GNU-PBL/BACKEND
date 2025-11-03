package gnu.project.backend.product.dto.request;

import static gnu.project.backend.product.constant.ProductConstant.ADDRESS_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.ADDRESS_REQUIRED;
import static gnu.project.backend.product.constant.ProductConstant.AVAILABLE_TIMES_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.DETAIL_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.DETAIL_REQUIRED;
import static gnu.project.backend.product.constant.ProductConstant.MAX_ADDRESS_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.MAX_AVAILABLE_TIMES;
import static gnu.project.backend.product.constant.ProductConstant.MAX_DETAIL_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.MAX_NAME_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.MAX_OPTION_COUNT;
import static gnu.project.backend.product.constant.ProductConstant.MIN_PRICE;
import static gnu.project.backend.product.constant.ProductConstant.NAME_LENGTH;
import static gnu.project.backend.product.constant.ProductConstant.NAME_REQUIRED;
import static gnu.project.backend.product.constant.ProductConstant.OPTION_LIMIT;
import static gnu.project.backend.product.constant.ProductConstant.PRICE_MIN;
import static gnu.project.backend.product.constant.ProductConstant.PRICE_REQUIRED;

import gnu.project.backend.product.enumerated.Region;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record DressUpdateRequest(

    @NotBlank(message = NAME_REQUIRED)
    @Size(max = MAX_NAME_LENGTH, message = NAME_LENGTH)
    String name,

    @NotNull(message = PRICE_REQUIRED)
    @Min(value = MIN_PRICE, message = PRICE_MIN)
    Integer price,

    @NotBlank(message = ADDRESS_REQUIRED)
    @Size(max = MAX_ADDRESS_LENGTH, message = ADDRESS_LENGTH)
    String address,

    @NotBlank(message = DETAIL_REQUIRED)
    @Size(max = MAX_DETAIL_LENGTH, message = DETAIL_LENGTH)
    String detail,

    @Size(max = MAX_AVAILABLE_TIMES, message = AVAILABLE_TIMES_LENGTH)
    String availableTimes,
    @NotBlank
    Region region,
    @Valid
    @Size(max = MAX_OPTION_COUNT, message = OPTION_LIMIT)
    List<OptionUpdateRequest> options,

    List<Long> keepImagesId,
    List<TagRequest> tags
) {

}
