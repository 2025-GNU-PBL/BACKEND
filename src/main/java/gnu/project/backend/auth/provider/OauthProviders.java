package gnu.project.backend.auth.provider;

import static gnu.project.backend.common.error.ErrorCode.IS_NOT_VALID_SOCIAL;

import gnu.project.backend.auth.enumerated.SocialProvider;
import gnu.project.backend.common.exception.AuthException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OauthProviders {

    private final Map<SocialProvider, OauthProvider> providerMap;

    public OauthProviders(List<OauthProvider> providers) {
        this.providerMap = providers.stream()
            .collect(Collectors.toMap(
                OauthProvider::getProvider,
                Function.identity()
            ));
    }

    public OauthProvider getProvider(SocialProvider provider) {
        OauthProvider social = providerMap.get(provider);
        if (social == null) {
            throw new AuthException(IS_NOT_VALID_SOCIAL);
        }
        return social;
    }
}
