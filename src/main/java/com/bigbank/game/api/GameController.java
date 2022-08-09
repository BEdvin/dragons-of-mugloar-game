package com.bigbank.game.api;

import com.bigbank.game.service.GameService;
import com.bigbank.game.service.SseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executor;

@RestController
@RequestMapping("api/play-game")
public class GameController {

    private final GameService gameService;
    private final Executor executor;
    private final SseService sseService;

    public GameController(final GameService gameService, final @Qualifier("taskExecutor") Executor executor,
                          final SseService sseService) {
        this.gameService = gameService;
        this.executor = executor;
        this.sseService = sseService;
    }

    @GetMapping()
    public SseEmitter startGame() {
        final SseEmitter sseEmitter = sseService.getSseEmitter();
        executor.execute(() -> {
            try {
                gameService.runGame();
            } catch (Exception ex) {
                sseEmitter.completeWithError(ex);
            }
        });
        return sseEmitter;
    }
}
