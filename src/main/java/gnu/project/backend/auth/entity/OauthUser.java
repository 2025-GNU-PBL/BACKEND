package gnu.project.backend.auth.entity;

import gnu.project.backend.auth.enumerated.SocialProvider;
import gnu.project.backend.common.enumerated.UserRole;

public interface OauthUser {

    Long getId();

    OauthInfo getOauthInfo();

    UserRole getUserRole();

    default String getEmail() {
        return getOauthInfo().getEmail();
    }

    default String getName() {
        return getOauthInfo().getName();
    }

    default String getSocialId() {
        return getOauthInfo().getSocialId();
    }

    default SocialProvider getSocialProvider() {
        return getOauthInfo().getSocialProvider();
    }
}
