package com.bigbank.game.service.impl;

import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.converter.InitialStatusConverter;
import com.bigbank.game.converter.TaskStatisticsConverter;
import com.bigbank.game.data.dto.MessageStatisticsDto;
import com.bigbank.game.data.model.InitialStatus;
import com.bigbank.game.data.model.Task;
import com.bigbank.game.data.model.TaskStatistics;
import com.bigbank.game.data.repository.InitialStatusRepository;
import com.bigbank.game.data.repository.TaskRepository;
import com.bigbank.game.service.AbstractGameService;
import com.bigbank.game.service.ResourceService;
import com.bigbank.game.service.SseService;
import com.bigbank.game.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends AbstractGameService implements TaskService {

    @Value("${game.settings.turnTolerance}")
    private int turnTolerance;

    private final GameApiClient client;
    private final TaskRepository taskRepository;
    private final InitialStatusRepository initialStatusRepository;
    private final InitialStatusConverter initialStatusConverter;
    private final TaskStatisticsConverter taskStatisticsConverter;
    private final ResourceService resourceService;
    private final SseService sseService;
    private final InitialStatusRepository statusRepository;

    @Override
    public String startGame() {
        final InitialStatus initialStatus = initialStatusConverter.convert(client.callStartGame());
        final String gameId = initialStatus.getGameId();
        initialStatusRepository.save(initialStatus);
        sseService.send(String.format("Initializing new game with id %s", gameId));
        return gameId;
    }

    @Override
    public TaskStatistics solveTask(final String gameId, final String adId) {
        final TaskStatistics taskStatistics = makeSolveTaskRequest(gameId, adId);
        if (taskStatistics.isSkipped()) {
            sseService.send(String.format("Task %s is skipped", adId));
        } else {
            int turn = increaseTurn(gameId);
            sseService.send(
                    String.format("Round turn: %s. Total turn %s. Task %s. Current score is %s. Number of lives is %s",
                            turn,
                            taskStatistics.getTurn(),
                            taskStatistics.isSuccess() ? "succeeded!" : "failed",
                            taskStatistics.getScore(),
                            taskStatistics.getLives()));
        }
        final Task task = taskRepository.findByAdId(adId);
        task.setSolved(true);
        return taskStatistics;
    }

    @Override
    public Task selectBiggestScoreHavingTask(final String gameId) {
        final InitialStatus initialStatus = initialStatusRepository.findByGameId(gameId);
        int turn = initialStatus.getTurn();
        final Task task = getBiggestScoreHavingTaskRecursively(gameId, initialStatus, turn);
        sseService.send(String.format("New task %s is selected. Reward %s. Selected task difficulty is: %s. " +
                        "Task description: %s",
                task.getAdId(),
                task.getReward(),
                task.getProbability(),
                task.getMessage()));
        return task;
    }

    @Override
    public Task selectEasiestTask(final String gameId) {
        final InitialStatus initialStatus = initialStatusRepository.findByGameId(gameId);
        int turn = initialStatus.getTurn();
        final Task task = getEasiestTaskRecursively(gameId, initialStatus, turn);
        sseService.send(String.format("New task %s is selected. Selected task difficulty is: %s. Task description: %s",
                task.getAdId(),
                task.getProbability(),
                task.getMessage()));
        return task;
    }

    @Override
    protected InitialStatusRepository getStatusRepository() {
        return statusRepository;
    }

    private TaskStatistics makeSolveTaskRequest(final String gameId, final String adId) {
        final ResponseEntity<MessageStatisticsDto> responseEntity = client.solveTask(gameId, adId);
        if (responseEntity.getStatusCode().isError()) {
            sseService.send(String.format("Some problem occurred by solving the task: %s. Http status code is: %s",
                    adId,
                    responseEntity.getStatusCode()));
            return TaskStatistics.builder()
                    .skipped(true)
                    .responseStatusCode(responseEntity.getStatusCode().value()).build();
        }
        return taskStatisticsConverter.convert(responseEntity.getBody());
    }

    private Task getBiggestScoreHavingTaskRecursively(final String gameId, final InitialStatus initialStatus,
                                                      final int turn) {
        final int adjustedTurn = turn + turnTolerance;
        Comparator<Task> comparator = Comparator.comparing(Task::getReward)
                .reversed()
                .thenComparingInt(Task::getDifficulty)
                .thenComparingInt(Task::getExpiresIn);
        return taskRepository.findAll().stream()
                .filter(task -> !task.isSolved())
                .filter(task -> task.getExpiresIn() > adjustedTurn)
                .min(comparator)
                .orElseGet(() -> {
                    resourceService.reloadTasks(gameId);
                    resetTurn(initialStatus);
                    return getBiggestScoreHavingTaskRecursively(gameId, initialStatus, initialStatus.getTurn());
                });
    }

    private Task getEasiestTaskRecursively(final String gameId, final InitialStatus initialStatus, final int turn) {
        final int adjustedTurn = turn + turnTolerance;
        Comparator<Task> comparator = Comparator.comparingInt(Task::getDifficulty)
                .thenComparing(Comparator.comparingInt(Task::getReward).reversed())
                .thenComparingInt(Task::getExpiresIn);
        return taskRepository.findAll().stream()
                .filter(task -> !task.isSolved())
                .filter(task -> task.getExpiresIn() > adjustedTurn)
                .min(comparator)
                .orElseGet(() -> {
                    resourceService.reloadTasks(gameId);
                    resetTurn(initialStatus);
                    return getEasiestTaskRecursively(gameId, initialStatus, initialStatus.getTurn());
                });
    }
}
