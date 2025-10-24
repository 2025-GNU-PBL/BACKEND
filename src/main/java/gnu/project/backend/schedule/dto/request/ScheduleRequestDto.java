package gnu.project.backend.schedule.dto.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record ScheduleRequestDto(
    String title,
    String content,
    List<MultipartFile> files
) {

}
