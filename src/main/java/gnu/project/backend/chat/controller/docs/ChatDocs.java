// src/main/java/gnu/project/backend/chat/controller/docs/ChatDocs.java
package gnu.project.backend.chat.controller.docs;

import gnu.project.backend.chat.dto.request.ChatMessageRequest;
import gnu.project.backend.chat.dto.request.ChatRoomCreateRequest;
import gnu.project.backend.chat.dto.response.ChatMessageResponse;
import gnu.project.backend.chat.dto.response.ChatRoomListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "채팅 API")
public interface ChatDocs {

    @Operation(summary = "채팅방 생성/가져오기")
    @PostMapping("/rooms")
    Long createRoom(@RequestBody ChatRoomCreateRequest request);

    @Operation(summary = "오너 채팅방 목록")
    @GetMapping("/rooms/owner/{ownerId}")
    List<ChatRoomListResponse> getOwnerRooms(@PathVariable String ownerId);

    @Operation(summary = "고객 채팅방 목록")
    @GetMapping("/rooms/customer/{customerId}")
    List<ChatRoomListResponse> getCustomerRooms(@PathVariable String customerId);

    @Operation(summary = "채팅 히스토리 조회 (페이징)")
    @GetMapping("/history/{chatRoomId}")
    List<ChatMessageResponse> getHistory(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size
    );

    @Operation(summary = "채팅방 읽음 처리")
    @PostMapping("/rooms/{chatRoomId}/read")
    void readAll(@PathVariable Long chatRoomId, @RequestParam String role);

    @Operation(summary = "테스트용 REST 메시지 전송")
    @PostMapping("/messages")
    ChatMessageResponse sendByRest(@RequestBody ChatMessageRequest request);

    @Operation(summary = "채팅방 한쪽만 숨기기(소프트 삭제)")
    @DeleteMapping("/rooms/{chatRoomId}")
    void deleteRoom(
            @PathVariable Long chatRoomId,
            @RequestParam String role,
            @RequestParam String senderId
    );
}
