package com.bigbank.game.service.impl;

import com.bigbank.game.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    @Value("${game.settings.gameStrategy}")
    private String gameStrategy;

    private final ApplicationContext applicationContext;
    private final TaskService taskService;
    private final ResourceService resourceService;
    private final SseService sseService;

    @Override
    public void runGame() {
        final String gameId = taskService.startGame();
        fetchAllResources(gameId);
        try {
            final GameStrategyService gameStrategyService = applicationContext
                    .getBean(gameStrategy, GameStrategyService.class);
            gameStrategyService.playGame(gameId);
        } catch (NoSuchBeanDefinitionException e) {
            sseService.send(String.format("No strategy found with name: %s", gameStrategy));
        }

    }

    private void fetchAllResources(final String gameId) {
        resourceService.loadItems(gameId);
        resourceService.loadTasks(gameId);
    }
}
