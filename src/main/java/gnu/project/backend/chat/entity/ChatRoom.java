package gnu.project.backend.chat.entity;

import gnu.project.backend.common.entity.BaseEntity;
import gnu.project.backend.product.enumerated.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "owner_deleted", nullable = false)
    private boolean ownerDeleted = false;

    @Column(name = "customer_deleted", nullable = false)
    private boolean customerDeleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_product_category", length = 50)
    private Category lastProductCategory;

    public static ChatRoom create(String ownerId, String customerId) {
        ChatRoom room = new ChatRoom();
        room.ownerId = ownerId;
        room.customerId = customerId;
        return room;
    }

    public void touchCategory(Category category) {
        this.lastProductCategory = category;
    }

    public void deleteByOwner() {
        this.ownerDeleted = true;
    }

    public void deleteByCustomer() {
        this.customerDeleted = true;
    }
}
