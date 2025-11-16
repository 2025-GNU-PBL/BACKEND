package gnu.project.backend.chat.controller;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.chat.controller.docs.ChatDocs;
import gnu.project.backend.chat.dto.request.ChatOpenFromProductRequest;
import gnu.project.backend.chat.dto.request.ChatSendRequest;
import gnu.project.backend.chat.dto.response.ChatMessageResponse;
import gnu.project.backend.chat.dto.response.ChatRoomListResponse;
import gnu.project.backend.chat.service.ChatService;
import gnu.project.backend.product.enumerated.Category;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatController implements ChatDocs {

    private final ChatService chatService;

    @Override
    public Long openFromProduct(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestBody ChatOpenFromProductRequest request
    ) {
        return chatService.openRoomFromProduct(accessor, request);
    }

    @Override
    public List<ChatRoomListResponse> myRoomsAsCustomer(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestParam(required = false) Category category
    ) {
        return chatService.getMyRoomsAsCustomer(accessor, category);
    }

    @Override
    public List<ChatRoomListResponse> myRoomsAsOwner(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestParam(required = false) Category category
    ) {
        return chatService.getMyRoomsAsOwner(accessor, category);
    }

    @Override
    public List<ChatMessageResponse> getHistory(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size
    ) {
        return chatService.getHistory(accessor, chatRoomId, cursor, size);
    }

    @Override
    public void readAll(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable Long chatRoomId
    ) {
        chatService.readAll(accessor, chatRoomId);
    }

    @Override
    public ChatMessageResponse sendByRest(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestBody ChatSendRequest request
    ) {
        return chatService.sendByRest(accessor, request);
    }

    @Override
    public void deleteRoom(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable Long chatRoomId
    ) {
        chatService.deleteMySideRoom(accessor, chatRoomId);
    }
}
