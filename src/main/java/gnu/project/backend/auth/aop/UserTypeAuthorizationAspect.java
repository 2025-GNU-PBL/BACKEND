package gnu.project.backend.auth.aop;

import static gnu.project.backend.common.error.ErrorCode.AUTH_FORBIDDEN;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.common.exception.AuthException;
import java.util.Arrays;
import java.util.function.Predicate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserTypeAuthorizationAspect {

    @Around("@annotation(gnu.project.backend.auth.aop.OnlyCustomer)")
    public Object authorizeCustomer(ProceedingJoinPoint joinPoint) throws Throwable {
        return authorize(joinPoint, Accessor::isCustomer);
    }

    @Around("@annotation(gnu.project.backend.auth.aop.OnlyOwner)")
    public Object authorizeOwner(ProceedingJoinPoint joinPoint) throws Throwable {
        return authorize(joinPoint, Accessor::isOwner);
    }

    private Object authorize(
        ProceedingJoinPoint joinPoint,
        Predicate<Accessor> condition
    ) throws Throwable {
        Arrays.stream(joinPoint.getArgs())
            .filter(Accessor.class::isInstance)
            .map(Accessor.class::cast)
            .filter(condition)
            .findFirst()
            .orElseThrow(() -> new AuthException(AUTH_FORBIDDEN));

        return joinPoint.proceed();
    }
}
