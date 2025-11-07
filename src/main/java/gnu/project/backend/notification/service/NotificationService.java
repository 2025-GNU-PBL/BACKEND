package gnu.project.backend.notification.service;

import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.NOTIFICATION_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.OWNER_NOT_FOUND_EXCEPTION;
import static gnu.project.backend.common.error.ErrorCode.UNAUTHORIZED;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.notification.dto.response.NotificationResponseDto;
import gnu.project.backend.notification.entity.Notification;
import gnu.project.backend.notification.repository.NotificationRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final OwnerRepository ownerRepository;
    private final CustomerRepository customerRepository;
    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;

    private static boolean validation(
        final Accessor accessor,
        final Notification notification,
        final Long id
    ) {
        return !notification.getRecipientId().equals(id) && !notification.getRecipientRole()
            .equals(accessor.getUserRole());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUnreadNotifications(final Accessor accessor) {
        Long userId = getId(accessor);

        List<Notification> notifications = notificationRepository.findUnreadNotifications(
            userId,
            accessor.getUserRole()
        );

        return notifications.stream()
            .filter(n -> !n.isExpired())
            .map(NotificationResponseDto::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getAllNotifications(final Accessor accessor) {
        Long userId = getId(accessor);

        List<Notification> notifications = notificationRepository
            .findAllNotifications(userId, accessor.getUserRole());

        return notifications.stream()
            .filter(n -> !n.isExpired())
            .map(NotificationResponseDto::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(final Accessor accessor) {
        Long id = getId(accessor);
        return notificationRepository.countUnreadNotifications(id, accessor.getUserRole());
    }

    public NotificationResponseDto markAsRead(final Accessor accessor, final Long notificationId) {
        Long id = getId(accessor);

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BusinessException(NOTIFICATION_NOT_FOUND_EXCEPTION));

        if (validation(accessor, notification, id)) {
            throw new BusinessException(UNAUTHORIZED);
        }

        notification.markAsRead();
        Notification savedNotification = notificationRepository.save(notification);

        return NotificationResponseDto.from(savedNotification);
    }

    /**
     * 모든 알림 읽음 처리
     */
    public List<NotificationResponseDto> markAllAsRead(final Accessor accessor) {
        Long userId = getId(accessor);

        List<Notification> unreadNotifications = notificationRepository
            .findUnreadNotifications(userId, accessor.getUserRole());

        unreadNotifications.forEach(Notification::markAsRead);
        List<Notification> savedNotifications = notificationRepository.saveAll(unreadNotifications);

        return savedNotifications.stream().map(NotificationResponseDto::from).toList();
    }

    /**
     * 알림 삭제
     */
    public void deleteNotification(final Accessor accessor, final Long notificationId) {
        Long userId = getId(accessor);

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BusinessException(NOTIFICATION_NOT_FOUND_EXCEPTION));

        if (validation(accessor, notification, userId)) {
            throw new BusinessException(UNAUTHORIZED);
        }

        notificationRepository.delete(notification);
    }

    public void createAndSendNotification(Notification notification) {
        final Notification savedNotification = notificationRepository.save(notification);
        log.info("알림 저장 완료 - ID: {}, ID: {} , Role {}",
            savedNotification.getId(),
            savedNotification.getRecipientId(),
            savedNotification.getRecipientRole()
        );

        try {
            sseEmitterService.sendNotification(
                savedNotification.getRecipientRole(),
                savedNotification.getRecipientId(),
                savedNotification
            );
            savedNotification.markAsSent();
            notificationRepository.save(savedNotification);
            log.info("알림 전송 완료 - ID: {}", savedNotification.getId());
        } catch (Exception e) {
            log.warn("알림 실시간 전송 실패 (로그인 시 조회 가능) - ID: {}, Error: {}",
                savedNotification.getId(), e.getMessage());
        }
    }

    private Long getId(final Accessor accessor) {
        return switch (accessor.getUserRole()) {
            case OWNER -> {
                Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                    .orElseThrow(() -> new BusinessException(OWNER_NOT_FOUND_EXCEPTION));
                yield owner.getId();
            }
            case CUSTOMER -> {
                Customer customer = customerRepository.findByOauthInfo_SocialId(
                        accessor.getSocialId())
                    .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));
                yield customer.getId();
            }
            default -> throw new BusinessException(UNAUTHORIZED);
        };
    }

    public SseEmitter subscribe(final Accessor accessor) {
        final Long userId = getId(accessor);
        final SseEmitter emitter = sseEmitterService.createEmitter(accessor.getUserRole(), userId);

        final List<Notification> pending = notificationRepository.findUnsentNotifications(userId,
            accessor.getUserRole());
        pending.stream()
            .filter(n -> !n.isExpired())
            .forEach(n -> {
                    sseEmitterService.sendNotification(accessor.getUserRole(), userId, n);
                    n.markAsSent();
                }
            );
        notificationRepository.saveAll(pending);
        return emitter;
    }
}
