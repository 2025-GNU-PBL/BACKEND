package gnu.project.backend.product.dto.request;

import static gnu.project.backend.product.constant.ProductConstant.*;

import gnu.project.backend.product.enumerated.Region;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record WeddingHallUpdateRequest(

    @NotNull(message = PRICE_REQUIRED)
    @Min(value = MIN_PRICE, message = PRICE_MIN)
    Integer price,

    @NotBlank(message = ADDRESS_REQUIRED)
    @Size(max = MAX_ADDRESS_LENGTH, message = ADDRESS_LENGTH)
    String address,

    @NotBlank(message = DETAIL_REQUIRED)
    @Size(max = MAX_DETAIL_LENGTH, message = DETAIL_LENGTH)
    String detail,

    @NotBlank(message = NAME_REQUIRED)
    @Size(max = MAX_NAME_LENGTH, message = NAME_LENGTH)
    String name,

    @Size(max = MAX_AVAILABLE_TIMES, message = AVAILABLE_TIMES_LENGTH)
    String availableTimes,

    Integer capacity,
    Integer minGuest,
    Integer maxGuest,
    Integer hallType,
    Integer parkingCapacity,

    @Size(max = 100)
    String cateringType,

    String reservationPolicy,

    @NotNull(message = REGION_REQUIRE)
    Region region,

    List<TagRequest> tags,

    @Valid
    @Size(max = MAX_OPTION_COUNT, message = OPTION_LIMIT)
    List<OptionUpdateRequest> options,

    List<Long> keepImagesId
) {

}
