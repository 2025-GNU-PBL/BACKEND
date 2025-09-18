package gnu.project.backend.auth.dto.request;


import gnu.project.backend.auth.enurmerated.SocialProvider;
import gnu.project.backend.common.enurmerated.UserRole;

public record OauthLoginRequest(
    String code,
    SocialProvider socialProvider,
    UserRole userRole
    // String state //naver 필드
) {

}
