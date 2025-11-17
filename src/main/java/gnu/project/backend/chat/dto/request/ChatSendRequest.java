package gnu.project.backend.chat.dto.request;

public record ChatSendRequest(
        Long chatRoomId,
        String message
) {
}
