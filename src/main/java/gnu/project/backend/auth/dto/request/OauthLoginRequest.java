package gnu.project.backend.auth.dto.request;


import gnu.project.backend.auth.enurmerated.SocialProvider;

public record OauthLoginRequest(
    String code,
    SocialProvider socialProvider
) {

}
