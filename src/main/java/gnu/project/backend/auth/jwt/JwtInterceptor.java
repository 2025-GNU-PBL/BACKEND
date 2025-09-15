package gnu.project.backend.auth.jwt;

import static gnu.project.backend.auth.constant.JwtConstants.AUTHORIZATION_HEADER;
import static gnu.project.backend.auth.constant.JwtConstants.BEARER_PREFIX_LENGTH;
import static gnu.project.backend.auth.constant.JwtConstants.REQUEST_ATTR_SOCIAL_ID;
import static gnu.project.backend.auth.constant.JwtConstants.REQUEST_ATTR_USER_ROLE;
import static gnu.project.backend.auth.constant.KakaoOauthConstants.BEARER_PREFIX;
import static gnu.project.backend.common.error.ErrorCode.AUTH_TOKEN_INVALID;

import gnu.project.backend.common.enurmerated.UserRole;
import gnu.project.backend.common.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtResolver jwtResolver;

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) throws Exception {

        String token = extractToken(request);

        if (token == null) {
            setGuestAttributes(request);
            return true;
        }

        if (!jwtResolver.isValid(token)) {
            throw new AuthException(AUTH_TOKEN_INVALID);
        }

        setUserAttributes(request, token);
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return header.substring(BEARER_PREFIX_LENGTH);
    }

    private void setUserAttributes(HttpServletRequest request, String token) {
        UUID publicId = jwtResolver.extractPublicId(token);
        UserRole userRole = jwtResolver.extractUserType(token);

        request.setAttribute(REQUEST_ATTR_SOCIAL_ID, publicId);
        request.setAttribute(REQUEST_ATTR_USER_ROLE, userRole);
    }

    private void setGuestAttributes(HttpServletRequest request) {
        request.setAttribute(REQUEST_ATTR_USER_ROLE, UserRole.GUEST);
    }
}
