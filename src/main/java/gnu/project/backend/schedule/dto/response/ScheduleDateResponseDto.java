package gnu.project.backend.schedule.dto.response;

import gnu.project.backend.schedule.entity.Schedule;
import gnu.project.backend.schedule.enurmurated.ScheduleType;
import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleDateResponseDto(
    Long id,
    String title,
    String content,
    LocalDate startScheduleDate,
    LocalDate endScheduleDate,
    LocalTime startTime,
    LocalTime endTime,
    ScheduleType scheduleType

) {

    public static ScheduleDateResponseDto toResponse(final Schedule schedule) {
        return new ScheduleDateResponseDto(
            schedule.getId(),
            schedule.getTitle(),
            schedule.getContent(),
            schedule.getStartScheduleDate(),
            schedule.getEndScheduleDate(),
            schedule.getStartTime(),
            schedule.getEndTime(),
            schedule.getScheduleType()
        );
    }
}
