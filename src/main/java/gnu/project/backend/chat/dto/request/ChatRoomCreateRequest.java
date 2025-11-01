package gnu.project.backend.chat.dto.request;

public record ChatRoomCreateRequest(
        String ownerId,
        String customerId
) {}
