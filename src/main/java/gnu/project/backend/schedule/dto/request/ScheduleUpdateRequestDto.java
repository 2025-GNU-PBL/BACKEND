package gnu.project.backend.schedule.dto.request;

import java.time.LocalDate;
import java.util.List;

public record ScheduleUpdateRequestDto(
    String title,
    String content,
    LocalDate scheduleDate,
    List<Long> keepFileIds

) {

}
