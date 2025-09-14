package gnu.project.backend.auth.provider;

import gnu.project.backend.auth.enurmerated.SocialProvider;

public interface OauthProvider {

    SocialProvider getProvider();

    String getAccessToken(String code);


}
