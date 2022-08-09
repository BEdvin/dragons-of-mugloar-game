package com.bigbank.game.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {

    void send(String message);

    void complete();

    void initialiseSseEmitter();

    SseEmitter getSseEmitter();
}
