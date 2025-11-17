package gnu.project.backend.chat.entity;

import gnu.project.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "sender_role", nullable = false, length = 50)
    private String senderRole;

    @Column(name = "sender_id", nullable = false, length = 255)
    private String senderId;

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    @Column(name = "owner_read", nullable = false)
    private boolean ownerRead;

    @Column(name = "owner_read_at")
    private LocalDateTime ownerReadAt;

    @Column(name = "customer_read", nullable = false)
    private boolean customerRead;

    @Column(name = "customer_read_at")
    private LocalDateTime customerReadAt;

    public static Chatting create(
            ChatRoom chatRoom,
            String message,
            String senderRole,
            String senderId
    ) {
        if (chatRoom == null) {
            throw new IllegalArgumentException("ChatRoom은 필수입니다.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지는 필수입니다.");
        }
        if (message.length() > 255) {
            throw new IllegalArgumentException("메시지는 255자를 초과할 수 없습니다.");
        }
        if (senderRole == null || senderRole.trim().isEmpty()) {
            throw new IllegalArgumentException("발신자 역할은 필수입니다.");
        }
        if (senderId == null || senderId.trim().isEmpty()) {
            throw new IllegalArgumentException("발신자 ID는 필수입니다.");
        }

        Chatting c = new Chatting();
        c.chatRoom = chatRoom;
        c.message = message;
        c.senderRole = senderRole;
        c.senderId = senderId;
        c.sendTime = LocalDateTime.now();
        c.ownerRead = false;
        c.customerRead = false;
        c.ownerReadAt = null;
        c.customerReadAt = null;
        return c;
    }

    public void readByOwner(LocalDateTime now) {
        this.ownerRead = true;
        this.ownerReadAt = now;
    }

    public void readByCustomer(LocalDateTime now) {
        this.customerRead = true;
        this.customerReadAt = now;
    }
}
