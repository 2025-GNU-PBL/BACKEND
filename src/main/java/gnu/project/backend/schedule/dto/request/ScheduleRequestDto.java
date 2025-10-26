package gnu.project.backend.schedule.dto.request;

import java.time.LocalDate;

public record ScheduleRequestDto(
    String title,
    String content,
    LocalDate scheduleDate
) {

}
