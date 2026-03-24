package com.kratosgado.chatapp.configs.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Arrays

@Aspect
@Component
class LoggingAspect {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut(
        "within(@org.springframework.stereotype.Repository *)" +
                " || within(@org.springframework.stereotype.Service *)" +
                " || within(@org.springframework.web.bind.annotation.RestController *)" +
                // " || within(@org.springframework.stereotype.Controller *)" +
                " || execution(* com.kratosgado.chatapp..*Repository.*(..))",
    )
    fun springBeanPointcut() {
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut(
        "within(com.kratosgado.chatapp..*)",
    )
    fun applicationPackagePointcut() {
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    fun logAfterThrowing(
        joinPoint: JoinPoint,
        e: Throwable,
    ) {
        logger.error(
            "****Exception in {}.{}() with cause = {}",
            joinPoint.signature.declaringTypeName,
            joinPoint.signature.name,
            if (e.cause != null) e.cause else "NULL",
            e,
        )
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        if (logger.isDebugEnabled) {
            logger.debug(
                ">>>> Enter: {}.{}() with argument[s] = {}",
                joinPoint.signature.declaringTypeName,
                joinPoint.signature.name,
                Arrays.toString(joinPoint.args),
            )
        }
        try {
            val start = System.currentTimeMillis()
            val result = joinPoint.proceed()
            val end = System.currentTimeMillis()
            if (logger.isDebugEnabled) {
                logger.debug(
                    "<<<<: {}.{}() with result = {} ({} ms)",
                    joinPoint.signature.declaringTypeName,
                    joinPoint.signature.name,
                    result,
                    end - start,
                )
            }
            return result
        } catch (e: IllegalArgumentException) {
            logger.error(
                "Illegal argument: {} in {}.{}()",
                Arrays.toString(joinPoint.args),
                joinPoint.signature.declaringTypeName,
                joinPoint.signature.name,
            )
            throw e
        }
    }
}
