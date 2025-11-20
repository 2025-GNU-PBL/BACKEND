package gnu.project.backend.schedule.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleRequestDto(
    @NotNull
    String title,
    @NotNull
    String content,
    @NotNull
    @FutureOrPresent
    LocalDate startScheduleDate,
    @NotNull
    @FutureOrPresent
    LocalDate endScheduleDate,
    @NotNull
    LocalTime startTime,
    @NotNull
    LocalTime endTime
) {

}
