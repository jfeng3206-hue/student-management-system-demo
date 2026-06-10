package com.example.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class DemoAspect {
    private static final Logger log = LoggerFactory.getLogger(DemoAspect.class);

    @Pointcut("execution(* com.example.demo.service.impl.StudentServiceImpl.*(..))")
    public void studentServiceMethods() {}

    @Before("studentServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("[DEMO - BEFORE] {}: args={}", joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "studentServiceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("[AFTER_RETURNING] {}: result={}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "studentServiceMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        log.error("[AFTER_THROWING] {}: exception={} message={}",
                joinPoint.getSignature().getName(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }

    @Around("studentServiceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.info("[AROUND] {}: elapsed={}ms", joinPoint.getSignature().getName(),
                    System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("[AROUND] {}: failed after {}ms", joinPoint.getSignature().getName(),
                    System.currentTimeMillis() - start);
            throw ex;
        }
    }
}
