package gnu.project.backend.schedule.dto.response;

import gnu.project.backend.schedule.entity.Schedule;
import gnu.project.backend.schedule.entity.ScheduleFile;
import gnu.project.backend.schedule.enurmurated.ScheduleType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ScheduleResponseDto(
    Long id,
    String title,
    String content,
    LocalDate startScheduleDate,
    LocalDate endScheduleDate,
    LocalTime startTime,
    LocalTime endTime,
    ScheduleType scheduleType,
    String productName,
    String bzName,
    String address,
    List<ScheduleFileResponse> scheduleFiles

) {


    public static ScheduleResponseDto toResponse(final Schedule schedule) {
        boolean isShared = schedule.getScheduleType() == ScheduleType.SHARED;

        return new ScheduleResponseDto(
            schedule.getId(),
            schedule.getTitle(),
            schedule.getContent(),
            schedule.getStartScheduleDate(),
            schedule.getEndScheduleDate(),
            schedule.getStartTime(),
            schedule.getEndTime(),
            schedule.getScheduleType(),
            isShared ? schedule.getProduct().getName() : null,
            isShared ? schedule.getOwner().getBzName() : null,
            isShared ? schedule.getOwner().getDetailAddress() : null,
            schedule.getFiles().stream()
                .map(ScheduleFileResponse::from)
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
