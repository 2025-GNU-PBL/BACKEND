package gnu.project.backend.customer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerUpdateRequest {
    private String profilePicture;
    private String phoneNumber;
    private String address;
    private String bankAccount;
}
