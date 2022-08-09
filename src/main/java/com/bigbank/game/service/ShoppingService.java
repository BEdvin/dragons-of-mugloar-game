package com.bigbank.game.service;

import com.bigbank.game.data.model.TaskStatistics;

public interface ShoppingService {

    void buyHealingPotionIfNeeded(String gameId, TaskStatistics taskStatistics);

    boolean buyArtefactIfHaveGold(String gameId, TaskStatistics taskStatistics);
}
