package gnu.project.backend.chat.controller;

import gnu.project.backend.chat.constant.ChatConstants;
import gnu.project.backend.chat.controller.docs.ChatDocs;
import gnu.project.backend.chat.dto.request.ChatMessageRequest;
import gnu.project.backend.chat.dto.request.ChatRoomCreateRequest;
import gnu.project.backend.chat.dto.response.ChatMessageResponse;
import gnu.project.backend.chat.dto.response.ChatRoomListResponse;
import gnu.project.backend.chat.service.ChatService;
import gnu.project.backend.common.error.ErrorCode;
import gnu.project.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController implements ChatDocs {

    private final ChatService chatService;

    @Override
    public Long createRoom(@RequestBody ChatRoomCreateRequest request) {
        return chatService.createRoomByDto(request);
    }

    @Override
    public List<ChatRoomListResponse> getOwnerRooms(@PathVariable String ownerId) {
        return chatService.getRoomsByOwner(ownerId);
    }

    @Override
    public List<ChatRoomListResponse> getCustomerRooms(@PathVariable String customerId) {
        return chatService.getRoomsByCustomer(customerId);
    }

    @Override
    public List<ChatMessageResponse> getHistory(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size
    ) {
        return chatService.getHistory(chatRoomId, cursor, size);
    }

    @Override
    public void readAll(@PathVariable Long chatRoomId, @RequestParam String role) {
        if (!ChatConstants.ROLE_OWNER.equalsIgnoreCase(role)
                && !ChatConstants.ROLE_CUSTOMER.equalsIgnoreCase(role)) {
            throw new BusinessException(ErrorCode.ROLE_IS_NOT_VALID);
        }
        chatService.readAll(chatRoomId, role);
    }

    @Override
    public ChatMessageResponse sendByRest(@RequestBody ChatMessageRequest request) {
        return chatService.saveMessage(request);
    }

    @Override
    public void deleteRoom(@PathVariable Long chatRoomId,
                           @RequestParam String role,
                           @RequestParam String senderId) {
        chatService.deleteRoomForSide(chatRoomId, role, senderId);
    }

}
