package gnu.project.backend.schedule.dto.response;

import gnu.project.backend.schedule.entity.Schedule;
import gnu.project.backend.schedule.entity.ScheduleFile;
import java.util.List;

public record ScheduleResponseDto(
    Long id,
    String title,
    String content,
    List<ScheduleFileResponse> scheduleFiles

) {


    public static ScheduleResponseDto toResponse(final Schedule schedule) {
        return new ScheduleResponseDto(
            schedule.getId(),
            schedule.getTitle(),
            schedule.getContent(),
            schedule.getFiles().stream().
                map(ScheduleResponseDto.ScheduleFileResponse::from)
                .toList()
        );
    }

    public record ScheduleFileResponse(
        Long id,
        String name,
        String s3Key
    ) {

        public static ScheduleResponseDto.ScheduleFileResponse from(ScheduleFile file) {
            return new ScheduleResponseDto.ScheduleFileResponse(
                file.getId(),
                file.getFileName(),
                file.getFilePath()
            );
        }
    }
}
