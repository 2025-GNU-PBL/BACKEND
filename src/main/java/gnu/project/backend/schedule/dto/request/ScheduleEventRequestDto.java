package gnu.project.backend.schedule.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ScheduleEventRequestDto(
    @NotBlank
    Long reservationId,
    @NotBlank
    @FutureOrPresent
    LocalDate scheduleTime,
    @NotBlank
    String title,
    @NotBlank
    String content
) {

}
