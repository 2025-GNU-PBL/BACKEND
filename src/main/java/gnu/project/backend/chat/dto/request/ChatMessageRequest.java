// src/main/java/gnu/project/backend/chat/dto/request/ChatMessageRequest.java
package gnu.project.backend.chat.dto.request;

public record ChatMessageRequest(
        Long chatRoomId,
        String senderRole,
        String senderId,
        String message
) {}
