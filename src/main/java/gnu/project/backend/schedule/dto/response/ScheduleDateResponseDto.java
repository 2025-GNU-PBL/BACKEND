package gnu.project.backend.schedule.dto.response;

import gnu.project.backend.schedule.entity.Schedule;
import java.time.LocalDate;

public record ScheduleDateResponseDto(
    Long id,
    String title,
    String content,
    LocalDate scheduleDate
) {

    public static ScheduleDateResponseDto toResponse(final Schedule schedule) {
        return new ScheduleDateResponseDto(
            schedule.getId(),
            schedule.getTitle(),
            schedule.getContent(),
            schedule.getScheduleDate()
        );
    }
}
