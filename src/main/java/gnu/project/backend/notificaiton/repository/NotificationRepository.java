package gnu.project.backend.notificaiton.repository;

import gnu.project.backend.notificaiton.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>,
    NotificationCustomRepository {

}
