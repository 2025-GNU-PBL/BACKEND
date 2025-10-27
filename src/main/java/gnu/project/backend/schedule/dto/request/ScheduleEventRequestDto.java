package gnu.project.backend.schedule.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ScheduleEventRequestDto(
    @NotNull
    Long reservationId,
    @NotNull
    @FutureOrPresent
    LocalDate scheduleTime,
    @NotNull
    String title,
    @NotNull
    String content
) {

}
