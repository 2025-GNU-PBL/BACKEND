package gnu.project.backend.chat.controller;

import gnu.project.backend.chat.dto.request.ChatMessageRequest;
import gnu.project.backend.chat.dto.response.ChatMessageResponse;
import gnu.project.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.message")
    public void handleMessage(ChatMessageRequest request) {
        ChatMessageResponse saved = chatService.saveMessage(request);
        messagingTemplate.convertAndSend("/sub/chatroom/" + saved.chatRoomId(), saved);
    }
}
