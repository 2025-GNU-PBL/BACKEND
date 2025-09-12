package gnu.project.backend.customer.dto;

import gnu.project.backend.customer.entity.Customer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class CustomerResponse {
    private String id;
    private String socialId2;
    private String role;
    private String profilePicture;
    private String age;
    private String phoneNumber;
    private String address;
    // bankAccount는 응답 시 제외하는 것이 보안상 더 좋습니다.

    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.socialId2 = customer.getSocialId2();
        this.role = customer.getRole();
        this.profilePicture = customer.getProfilePicture();
        this.age = customer.getAge();
        this.phoneNumber = customer.getPhoneNumber();
        this.address = customer.getAddress();
        // this.bankAccount = customer.getBankAccount(); // 필요 시 추가
    }
}