package gnu.project.backend.auth.dto.request;


import gnu.project.backend.auth.enumerated.SocialProvider;
import gnu.project.backend.common.enumerated.UserRole;

public record OauthLoginRequest(
    String code,
    SocialProvider socialProvider,
    UserRole userRole
    // String state //naver 필드
) {

}
