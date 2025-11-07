package gnu.project.backend.notification.service;

import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.notification.dto.response.NotificationResponseDto;
import gnu.project.backend.notification.entity.Notification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private static String generateKey(UserRole userRole, Long userId) {
        return userRole.name() + ":" + userId;
    }

    /**
     * SSE 연결 생성
     */
    public SseEmitter createEmitter(UserRole userRole, Long userId) {
        String key = generateKey(userRole, userId);
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onCompletion(() -> emitters.remove(key));
        emitter.onTimeout(() -> emitters.remove(key));
        emitter.onError((e) -> emitters.remove(key));

        emitters.put(key, emitter);

        log.info("SSE 연결 생성 완료 - key: {}", key);
        return emitter;
    }

    public void sendNotification(UserRole role, Long userId, Notification data) {
        String key = generateKey(role, userId);
        SseEmitter emitter = emitters.get(key);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(NotificationResponseDto.from(data)));
                log.info("알림 실시간 전송 - key: {}", key);
            } catch (Exception e) {
                emitters.remove(key);
                log.warn("알림 전송 실패, Emitter 제거 - key: {}", key);
            }
        }
    }
}
