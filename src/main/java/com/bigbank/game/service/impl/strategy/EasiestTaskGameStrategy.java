package com.bigbank.game.service.impl.strategy;

import com.bigbank.game.data.model.Task;
import com.bigbank.game.data.model.TaskStatistics;
import com.bigbank.game.data.constant.TaskSelectionStrategy;
import com.bigbank.game.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.bigbank.game.data.constant.GameConstants.GAME_OVER_CODE;

@Service
@RequiredArgsConstructor
public class EasiestTaskGameStrategy implements GameStrategyService {

    @Value("${game.settings.goal}")
    private int goal;
    @Value("${game.settings.failureTolerance}")
    private int failureTolerance;

    private final ShoppingService shoppingService;
    private final TaskService taskService;
    private final SseService sseService;
    private final ResourceService resourceService;

    @Override
    public void playGame(final String gameId) {
        int score = 0;
        int failures = 0;
        sseService.send(TaskSelectionStrategy.EASIEST_TASK_SELECTION.getTitle());
        while (score <= goal) {

            final Task task = taskService.selectEasiestTask(gameId);
            final TaskStatistics taskStatistics = taskService.solveTask(gameId, task.getAdId());

            if (!taskStatistics.isSuccess()) {
                failures++;
            }
            if (taskStatistics.isSkipped() && taskStatistics.getResponseStatusCode() == GAME_OVER_CODE) {
                sseService.send("Game over");
                break;
            }
            if (!taskStatistics.isSkipped()) {
                shoppingService.buyHealingPotionIfNeeded(gameId, taskStatistics);
                score = taskStatistics.getScore();

                if (failures >= failureTolerance) {
                    final boolean buySucceeded = shoppingService.buyArtefactIfHaveGold(gameId, taskStatistics);
                    if (buySucceeded) {
                        failures = 0;
                    }
                }
            }
        }
        if (score >= goal) {
            sseService.send(String.format("Congratulation! You won the game with score %s!", score));
            resourceService.investigateReputation(gameId);
        }
        sseService.complete();
    }
}
