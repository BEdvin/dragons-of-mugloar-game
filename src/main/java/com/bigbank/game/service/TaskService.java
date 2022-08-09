package com.bigbank.game.service;

import com.bigbank.game.data.model.Task;
import com.bigbank.game.data.model.TaskStatistics;

public interface TaskService {

    String startGame();

    TaskStatistics solveTask(String gameId, String adId);

    Task selectBiggestScoreHavingTask(String gameId);

    Task selectEasiestTask(String gameId);
}
