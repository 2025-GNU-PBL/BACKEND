package gnu.project.backend.notificaiton.controller;

import gnu.project.backend.notificaiton.service.NotificationService;
import gnu.project.backend.notificaiton.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam(name = "userId") Long userId) {
        return sseEmitterService.createEmitter(userId);
    }

}
