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
            String senderId,
            LocalDateTime sendTime,
            boolean ownerRead,
            LocalDateTime ownerReadAt,
            boolean customerRead,
            LocalDateTime customerReadAt
    ) {
        Chatting c = new Chatting();
        c.chatRoom = chatRoom;
        c.message = message;
        c.senderRole = senderRole;
        c.senderId = senderId;
        c.sendTime = sendTime;
        c.ownerRead = ownerRead;
        c.ownerReadAt = ownerReadAt;
        c.customerRead = customerRead;
        c.customerReadAt = customerReadAt;
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
