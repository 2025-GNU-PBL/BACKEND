package gnu.project.backend.auth.aop;

import static gnu.project.backend.auth.constant.JwtConstants.REQUEST_ATTR_SOCIAL_ID;
import static gnu.project.backend.auth.constant.JwtConstants.REQUEST_ATTR_USER_ROLE;
import static gnu.project.backend.common.error.ErrorCode.AUTH_USER_NOT_FOUND;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.auth.service.OauthService;
import gnu.project.backend.common.enurmerated.UserRole;
import gnu.project.backend.common.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final OauthService oauthService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
            && parameter.getParameterType().equals(Accessor.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final String socialId = (String) request.getAttribute(REQUEST_ATTR_SOCIAL_ID);
        final UserRole userRole = (UserRole) request.getAttribute(REQUEST_ATTR_USER_ROLE);
        if (socialId == null || userRole == null) {
            throw new AuthException(AUTH_USER_NOT_FOUND);
        }
        return oauthService.getCurrentAccessor(socialId, userRole);
    }
}
