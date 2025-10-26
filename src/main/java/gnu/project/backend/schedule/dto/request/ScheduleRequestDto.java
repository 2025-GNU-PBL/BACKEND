package gnu.project.backend.schedule.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ScheduleRequestDto(
    @NotBlank
    String title,
    @NotBlank
    String content,
    @NotBlank
    @FutureOrPresent
    LocalDate scheduleDate
) {

}
