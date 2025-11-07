package gnu.project.backend.notificaiton.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.enumerated.UserRole;
import gnu.project.backend.common.error.ErrorCode;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.owner.entity.Owner;
import gnu.project.backend.owner.repository.OwnerRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    private static String generateKey(UserRole userRole, Long userId) {
        return userRole.name() + ":" + userId;
    }

    public SseEmitter createEmitter(final Accessor accessor) {
        Long userId;
        String key;
        switch (accessor.getUserRole()) {
            case OWNER -> {
                Owner owner = ownerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.OWNER_NOT_FOUND_EXCEPTION));
                userId = owner.getId();
            }
            case CUSTOMER -> {
                Customer customer = customerRepository.findByOauthInfo_SocialId(
                        accessor.getSocialId())
                    .orElseThrow(
                        () -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION));
                userId = customer.getId();
            }
            default -> throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        key = generateKey(accessor.getUserRole(), userId);
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onCompletion(() -> emitters.remove(key));
        emitter.onTimeout(() -> emitters.remove(key));
        emitter.onError((e) -> emitters.remove(key));

        emitters.put(key, emitter);
        return emitter;
    }

    public void sendNotification(Long userId, Object data) {
        String key = generateKey(UserRole.CUSTOMER, userId);
        SseEmitter emitter = emitters.get(key);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(data));
            } catch (Exception e) {
                emitters.remove(key); // 송신 실패 시 제거
            }
        }
    }
}
