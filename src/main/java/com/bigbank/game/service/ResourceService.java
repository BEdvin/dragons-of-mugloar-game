package com.bigbank.game.service;

public interface ResourceService {

    void loadTasks(String gameId);

    void reloadTasks(String gameId);

    void loadItems(String gameId);

    void investigateReputation(final String gameId);
}
