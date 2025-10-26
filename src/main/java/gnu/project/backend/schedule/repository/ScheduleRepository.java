package gnu.project.backend.schedule.repository;

import gnu.project.backend.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>,
    ScheduleCustomRepository {


}
