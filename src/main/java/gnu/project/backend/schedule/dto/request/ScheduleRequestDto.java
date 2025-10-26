package gnu.project.backend.schedule.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ScheduleRequestDto(
    @NotNull
    String title,
    @NotNull
    String content,
    @NotNull
    @FutureOrPresent
    LocalDate scheduleDate
) {

}
