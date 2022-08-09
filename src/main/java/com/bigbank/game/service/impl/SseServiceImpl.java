package com.bigbank.game.service.impl;

import com.bigbank.game.service.SseService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
public class SseServiceImpl implements SseService {

    @Getter
    private SseEmitter sseEmitter;

    public SseServiceImpl() {
        sseEmitter = new SseEmitter();
    }

    @Override
    public void send(final String message) {
        final SseEmitter.SseEventBuilder event = SseEmitter.event().data(message);
        try {
            sseEmitter.send(event);
            log.info(message);
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
            log.error(e.getMessage());
        }
    }

    @Override
    public void complete() {
        sseEmitter.complete();
    }

    @Override
    public void initialiseSseEmitter() {
        this.sseEmitter = new SseEmitter();
    }
}
