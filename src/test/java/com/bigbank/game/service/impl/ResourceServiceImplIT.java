package com.bigbank.game.service.impl;

import com.bigbank.game.GameApplication;
import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.config.TestConfig;
import com.bigbank.game.data.dto.ItemDto;
import com.bigbank.game.data.dto.MessageDto;
import com.bigbank.game.data.dto.ReputationDto;
import com.bigbank.game.data.model.Item;
import com.bigbank.game.data.model.Reputation;
import com.bigbank.game.data.model.Task;
import com.bigbank.game.data.repository.ItemRepository;
import com.bigbank.game.data.repository.ReputationRepository;
import com.bigbank.game.data.repository.TaskRepository;
import com.bigbank.game.mock.MockJsonMapper;
import com.bigbank.game.service.ResourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GameApplication.class})
@Transactional
@AutoConfigureTestEntityManager
@Import(TestConfig.class)
public class ResourceServiceImplIT {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private MockJsonMapper<MessageDto> messageJsonMapper;
    @Autowired
    private MockJsonMapper<ItemDto> itemJsonMapper;
    @Autowired
    private MockJsonMapper<ReputationDto> reputationJsonMapper;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ReputationRepository reputationRepository;
    @MockBean
    private GameApiClient client;

    @Test
    public void givenGameId_whenLoadTasks_thenAllTasksArePersisted() {
        given(client.callGetMessages("gameId")).willReturn(
                messageJsonMapper.mockListResponse("messages.json", MessageDto.class));

        resourceService.loadTasks("gameId");

        final List<Task> tasks = taskRepository.findAll();
        assertThat(tasks.size()).isEqualTo(10);
    }

    @Test
    public void givenGameId_whenReloadTasks_thenOldTasksDeletedAndNewTasksArePersisted() {
        given(client.callGetMessages("gameId")).willReturn(
                messageJsonMapper.mockListResponse("messages.json", MessageDto.class));

        resourceService.reloadTasks("gameId");

        final List<Task> tasks = taskRepository.findAll();
        assertThat(tasks.size()).isEqualTo(10);
    }

    @Test
    public void givenGameId_whenLoadItems_thenAllItemsArePersisted() {
        given(client.callGetItems("gameId")).willReturn(
                itemJsonMapper.mockListResponse("items.json", ItemDto.class));

        resourceService.loadItems("gameId");

        final List<Item> items = itemRepository.findAll();
        assertThat(items.size()).isEqualTo(11);
    }

    @Test
    public void givenGameId_whenInvestigateReputation_thenInvestigationObjectPersisted() {
        given(client.investigateReputation("gameId")).willReturn(
                reputationJsonMapper.mockResponse("reputation.json", ReputationDto.class));

        resourceService.investigateReputation("gameId");

        final List<Reputation> reputation = reputationRepository.findAll();
        assertThat(reputation.size()).isEqualTo(1);
    }
}
