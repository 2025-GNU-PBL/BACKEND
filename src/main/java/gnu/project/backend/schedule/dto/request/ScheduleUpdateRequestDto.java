package gnu.project.backend.schedule.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record ScheduleUpdateRequestDto(
    @NotNull
    String title,
    @NotNull
    String content,
    @NotNull
    @FutureOrPresent
    LocalDate scheduleDate,
    List<Long> keepFileIds

) {

}
