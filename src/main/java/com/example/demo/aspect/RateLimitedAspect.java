package com.example.demo.aspect;

import com.example.demo.internal.RequestRateLimitExceededException;
import com.example.demo.service.RequestRateService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitedAspect {

    private final RequestRateService requestRateService;

    @Pointcut(value = "execution(* *(..)) " +
            "&& @annotation(com.example.demo.annotation.RateLimited)")
    public void rateLimitedPointcut() {}

    /**
     * It wraps every method annotated by {@link com.example.demo.annotation.RateLimited} and checks if
     * request rate limit is exceeded or not. If true it will throw {@link RequestRateLimitExceededException}
     * */
    @Around(value = "rateLimitedPointcut()")
    public Object rateLimitedAnnAspectAround(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (requestRateService.isRequestExceededLimit(httpServletRequest))
            throw new RequestRateLimitExceededException();
        return pjp.proceed();
    }
}
