package gnu.project.backend.notification.repository.impl;

import static gnu.project.backend.notification.entity.QNotification.notification;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.notification.entity.Notification;
import gnu.project.backend.notification.repository.NotificationCustomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public List<Notification> findUnreadNotifications(Long recipientId, UserRole userRole) {
        return query
            .selectFrom(notification)
            .where(
                notification.recipientId.eq(recipientId),
                notification.recipientRole.eq(userRole),
                notification.isRead.isFalse()
            )
            .orderBy(notification.createdAt.desc())
            .fetch();
    }

    @Override
    public List<Notification> findUnsentNotifications(Long recipientId, UserRole userRole) {
        return query.selectFrom(notification)
            .where(
                notification.recipientId.eq(recipientId),
                notification.recipientRole.eq(userRole),
                notification.isSent.isFalse()
            )
            .orderBy(notification.createdAt.desc())
            .fetch();
    }

    @Override
    public List<Notification> findAllNotifications(Long recipientId, UserRole userRole) {
        return query
            .selectFrom(notification)
            .where(
                notification.recipientId.eq(recipientId),
                notification.recipientRole.eq(userRole)
            )
            .orderBy(notification.createdAt.desc())
            .fetch();
    }

    @Override
    public Long countUnreadNotifications(Long recipientId, UserRole userRole) {
        return query
            .select(notification.count())
            .from(notification)
            .where(
                notification.recipientId.eq(recipientId),
                notification.recipientRole.eq(userRole),
                notification.isRead.isFalse()
            )
            .fetchOne();
    }
}
