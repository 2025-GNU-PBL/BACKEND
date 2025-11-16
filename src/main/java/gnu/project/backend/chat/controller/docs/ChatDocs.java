package gnu.project.backend.chat.controller.docs;

import gnu.project.backend.auth.aop.Auth;
import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.chat.dto.request.ChatOpenFromProductRequest;
import gnu.project.backend.chat.dto.request.ChatSendRequest;
import gnu.project.backend.chat.dto.response.ChatMessageResponse;
import gnu.project.backend.chat.dto.response.ChatRoomListResponse;
import gnu.project.backend.product.enumerated.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "채팅 API")
public interface ChatDocs {

    @Operation(summary = "상품 상세에서 채팅방 오픈/획득")
    @PostMapping("/api/chat/rooms/open-from-product")
    Long openFromProduct(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestBody ChatOpenFromProductRequest request
    );

    @Operation(summary = "내 채팅방 목록(고객)")
    @GetMapping("/api/chat/rooms/me/customer")
    List<ChatRoomListResponse> myRoomsAsCustomer(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestParam(required = false) Category category
    );

    @Operation(summary = "내 채팅방 목록(오너)")
    @GetMapping("/api/chat/rooms/me/owner")
    List<ChatRoomListResponse> myRoomsAsOwner(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestParam(required = false) Category category
    );

    @Operation(summary = "채팅 히스토리 조회")
    @GetMapping("/api/chat/history/{chatRoomId}")
    List<ChatMessageResponse> getHistory(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size
    );

    @Operation(summary = "채팅방 읽음 처리")
    @PostMapping("/api/chat/rooms/{chatRoomId}/read")
    void readAll(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable Long chatRoomId
    );

    @Operation(summary = "메시지 전송(Accessor 기반)")
    @PostMapping("/api/chat/messages")
    ChatMessageResponse sendByRest(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @RequestBody ChatSendRequest request
    );

    @Operation(summary = "채팅방 한쪽만 숨기기(Accessor 기반)")
    @DeleteMapping("/api/chat/rooms/{chatRoomId}")
    void deleteRoom(
            @Parameter(hidden = true) @Auth Accessor accessor,
            @PathVariable Long chatRoomId
    );
}
