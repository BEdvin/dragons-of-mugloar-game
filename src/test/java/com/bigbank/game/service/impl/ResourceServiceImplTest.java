package com.bigbank.game.service.impl;

import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.converter.ItemConverter;
import com.bigbank.game.converter.TaskConverter;
import com.bigbank.game.data.dto.ItemDto;
import com.bigbank.game.data.dto.MessageDto;
import com.bigbank.game.data.model.Item;
import com.bigbank.game.data.model.Task;
import com.bigbank.game.data.repository.ItemRepository;
import com.bigbank.game.data.repository.TaskRepository;
import com.bigbank.game.service.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private SseService sseService;
    @Mock
    private GameApiClient client;
    @Mock
    private ItemConverter itemConverter;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private TaskConverter taskConverter;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private ResourceServiceImpl resourceService;

    private ItemDto[] itemDtos;
    private MessageDto[] messageDtos;

    @BeforeEach
    public void setup() {
        itemDtos = getItemDtos();
        messageDtos = getMessageDtos();
    }

    @Test
    public void givenGameId_whenLoadTasks_thenNewTasksAreReplacedInDatabase() {
        given(client.callGetMessages("gameId")).willReturn(messageDtos);
        final List<Task> tasks = List.of(getTask("id1"), getTask("id2"));
        given(taskConverter.convert(messageDtos)).willReturn(tasks);
        doNothing().when(sseService).send(isA(String.class));
        doNothing().when(taskRepository).deleteAll();

        resourceService.loadTasks("gameId");

        verify(taskRepository, times(1)).deleteAll();
        verify(taskRepository, times(1)).saveAll(tasks);
        verify(sseService, times(2)).send(anyString());
    }

    @Test
    public void givenGameId_whenReloadTasks_thenNewTasksAreReplacedInDatabase() {
        given(client.callGetMessages("gameId")).willReturn(messageDtos);
        final List<Task> tasks = List.of(getTask("id1"), getTask("id2"));
        given(taskConverter.convert(messageDtos)).willReturn(tasks);
        doNothing().when(sseService).send(isA(String.class));
        doNothing().when(taskRepository).deleteAll();

        resourceService.reloadTasks("gameId");

        verify(taskRepository, times(1)).deleteAll();
        verify(taskRepository, times(1)).saveAll(tasks);
        verify(sseService, times(3)).send(anyString());
    }

    @Test
    public void givenGameId_whenLoadItems_thenNewItemsArePersistedToDatabase() {
        given(client.callGetItems("gameId")).willReturn(itemDtos);
        final List<Item> items = List.of(getItem("id1"), getItem("id2"));
        given(itemConverter.convert(itemDtos)).willReturn(items);
        doNothing().when(sseService).send(isA(String.class));
        doNothing().when(itemRepository).deleteAll();

        resourceService.loadItems("gameId");

        verify(itemRepository, times(1)).deleteAll();
        verify(itemRepository, times(1)).saveAll(items);
        verify(sseService, times(2)).send(anyString());
    }

    private Item getItem(String itemId) {
        return Item.builder()
                .available(true)
                .itemId(itemId)
                .cost(50)
                .name("Item name")
                .build();
    }

    private Task getTask(final String taskId) {
        return Task.builder()
                .adId(taskId)
                .message("Message to solve")
                .difficulty(1)
                .expiresIn(7)
                .reward(80)
                .solved(false)
                .build();
    }

    private MessageDto[] getMessageDtos() {
        return List.of(
                MessageDto.builder().adId("id1").reward(55).build(),
                MessageDto.builder().adId("id2").reward(21).build()
        ).toArray(new MessageDto[2]);
    }

    private ItemDto[] getItemDtos() {
        return List.of(
                ItemDto.builder().id("id1").cost(100).build(),
                ItemDto.builder().id("id2").cost(200).build()
        ).toArray(new ItemDto[2]);
    }
}
