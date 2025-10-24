package gnu.project.backend.schedule.dto.response;

import gnu.project.backend.schedule.entity.Schedule;

public record ScheduleResponseDto(
    Long id,
    String title,
    String content

) {

    public static ScheduleResponseDto toResponse(final Schedule schedule) {
        return new ScheduleResponseDto(
            schedule.getId(),
            schedule.getTitle(),
            schedule.getContent()
        );
    }
}
