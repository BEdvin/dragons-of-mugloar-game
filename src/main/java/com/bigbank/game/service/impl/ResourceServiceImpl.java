package com.bigbank.game.service.impl;

import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.converter.ItemConverter;
import com.bigbank.game.converter.ReputationConverter;
import com.bigbank.game.converter.TaskConverter;
import com.bigbank.game.data.model.Item;
import com.bigbank.game.data.model.Reputation;
import com.bigbank.game.data.model.Task;
import com.bigbank.game.data.repository.ItemRepository;
import com.bigbank.game.data.repository.ReputationRepository;
import com.bigbank.game.data.repository.TaskRepository;
import com.bigbank.game.service.ResourceService;
import com.bigbank.game.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final GameApiClient client;
    private final SseService sseService;
    private final TaskRepository taskRepository;
    private final TaskConverter taskConverter;
    private final ItemRepository itemRepository;
    private final ItemConverter itemConverter;
    private final ReputationRepository reputationRepository;
    private final ReputationConverter reputationConverter;

    @Override
    public void loadTasks(final String gameId) {
        sseService.send("Fetching tasks...");
        final List<Task> tasks = taskConverter.convert(client.callGetMessages(gameId));
        taskRepository.deleteAll();
        taskRepository.saveAll(tasks);
        sseService.send("Tasks successfully fetched");
    }

    @Override
    public void reloadTasks(final String gameId) {
        sseService.send("New tasks will be uploaded");
        loadTasks(gameId);
    }

    @Override
    public void loadItems(final String gameId) {
        sseService.send("Fetching items...");
        final List<Item> items = itemConverter.convert(client.callGetItems(gameId));
        itemRepository.deleteAll();
        itemRepository.saveAll(items);
        sseService.send("Items successfully fetched");
    }

    @Override
    public void investigateReputation(final String gameId) {
        final Reputation reputation = reputationConverter.convert(client.investigateReputation(gameId));
        reputationRepository.save(reputation);
        sseService.send(String.format("Reputation: people - %s, state - %s, underworld - %s",
                reputation.getPeople(),
                reputation.getState(),
                reputation.getUnderworld()));
    }
}
