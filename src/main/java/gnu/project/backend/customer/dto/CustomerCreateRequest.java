package gnu.project.backend.customer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerCreateRequest {
    private String id;
    private String socialId2;
    private String role;
    private String profilePicture;
    private String age;
    private String phoneNumber;
    private String address;
    private String bankAccount;


}
