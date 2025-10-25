package gnu.project.backend.schedule.repository;

import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.schedule.entity.Schedule;
import java.util.List;

public interface ScheduleCustomRepository {

    List<Schedule> findSchedulesById(
        final Long id,
        final Integer year,
        final Integer month,
        final UserRole userRole
    );

}
