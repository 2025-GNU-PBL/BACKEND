package gnu.project.backend.schedule.dto.request;

import java.time.LocalDate;

public record ScheduleEventRequestDto(
    Long reservationId,
    LocalDate scheduleTime,
    String title,
    String content
) {

}
