package gnu.project.backend.auth.entity;

import gnu.project.backend.common.enurmerated.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Accessor {

    private final String socialId;
    private final UserRole userRole;

    public static Accessor user(String socialId, UserRole userROle) {
        return new Accessor(socialId, userROle);
    }

    public boolean isCustomer() {
        return userRole == UserRole.CUSTOMER;
    }

    public boolean isOwner() {
        return userRole == UserRole.OWNER;
    }
}
