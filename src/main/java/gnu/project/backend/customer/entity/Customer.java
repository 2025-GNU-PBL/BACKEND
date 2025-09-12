package gnu.project.backend.customer.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Customer")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @Column(name = "id" , nullable = false)
    private String id;

    @Column(name = "social_id2", nullable = false)
    private String socialId2;

    @Column(name = "role")
    private String role;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "age")
    private String age; // SQL 타입이 VARCHAR이므로 String으로 매핑

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "bank_account")
    private String bankAccount;

    // Create를 위한 생성자
    public Customer(String id, String socialId2, String role, String profilePicture,
                    String age, String phoneNumber, String address, String bankAccount) {
        this.id = id;
        this.socialId2 = socialId2;
        this.role = role;
        this.profilePicture = profilePicture;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bankAccount = bankAccount;
    }

    // Update를 위한 메서드
    public void update(String profilePicture, String phoneNumber, String address, String bankAccount) {
        this.profilePicture = profilePicture;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bankAccount = bankAccount;
    }
}
