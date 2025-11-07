package gnu.project.backend.notificaiton.repository;

import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.notificaiton.entity.Notification;
import java.util.List;

public interface NotificationCustomRepository {

    /**
     * 읽지 않은 알림 조회
     */
    List<Notification> findUnreadNotifications(Long recipientId, UserRole userRole);

    /**
     * 전송되지 않은 알림 조회 (로그인 시 전송용)
     */
    List<Notification> findUnsentNotifications(Long customerId, UserRole userRole);

    /**
     * 모든 알림 조회
     */
    List<Notification> findAllNotifications(Long customerId, UserRole userRole);

    /**
     * 읽지 않은 알림 개수
     */
    Long countUnreadNotifications(Long customerId, UserRole userRole);

}
