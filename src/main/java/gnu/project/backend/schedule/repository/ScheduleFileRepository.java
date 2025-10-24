package gnu.project.backend.schedule.repository;

import gnu.project.backend.schedule.entity.ScheduleFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleFileRepository extends JpaRepository<ScheduleFile, Long> {

}
