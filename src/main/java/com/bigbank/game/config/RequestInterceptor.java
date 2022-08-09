package com.bigbank.game.config;

import com.bigbank.game.service.SseService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public record RequestInterceptor(
        ApplicationContext applicationContext) implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        final SseService sseService = applicationContext.getBean(SseService.class);
        sseService.initialiseSseEmitter();
        return true;
    }
}
