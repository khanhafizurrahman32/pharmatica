package org.example.pharmaticb.utilities.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggableAspect {

    @Around("@annotation(Loggable)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = signature.getDeclaringType().getSimpleName();

        log.info("Entering method: {}.{} with arguments: {}",
                className, methodName, Arrays.toString(joinPoint.getArgs()));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();

            if (result instanceof Mono) {
                var monoResult = (Mono<?>) result;

                return monoResult
                        .doOnSuccess(o -> getSuccess(o, className, methodName, System.currentTimeMillis() - start))
                        .doOnError(o -> getError(o, className, methodName, System.currentTimeMillis() - start));
            } else if (result instanceof Flux) {
                var fluxResult = (Flux<?>) result;
                return fluxResult
                        .doOnNext(o -> getSuccess(o, className, methodName, System.currentTimeMillis() - start))
                        .doOnError(o -> getError(o, className, methodName, System.currentTimeMillis() - start));
            }
            return result;
        } catch (Exception e) {
            log.error("Error executing log method", e);
            throw e;
        }
    }

    private static void getSuccess(Object response, String className, String methodName, long executionTime) {
        log.info("Exiting method: {}.{} with result: {}. Execution time: {} ms",
                className, methodName, response, executionTime);
    }

    private static void getError(Throwable error, String className, String methodName, long executionTime) {
        log.error("Error in method: {}.{}. Error: {}. Execution time: {} ms",
                className, methodName, error.getMessage(), executionTime);
    }
}
