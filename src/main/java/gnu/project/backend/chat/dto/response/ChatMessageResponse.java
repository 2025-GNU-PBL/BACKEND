package gnu.project.backend.chat.dto.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long chatRoomId,
        String senderRole,
        String senderId,
        String message,
        LocalDateTime sendTime,
        boolean ownerRead,
        boolean customerRead,
        LocalDateTime ownerReadAt,
        LocalDateTime customerReadAt,
        Long messageId
) {}
