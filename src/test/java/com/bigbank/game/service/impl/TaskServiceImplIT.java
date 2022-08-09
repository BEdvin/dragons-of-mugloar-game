package com.bigbank.game.service.impl;

import com.bigbank.game.GameApplication;
import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.config.TestConfig;
import com.bigbank.game.data.dto.InitialStatusDto;
import com.bigbank.game.data.dto.MessageDto;
import com.bigbank.game.data.dto.MessageStatisticsDto;
import com.bigbank.game.data.model.InitialStatus;
import com.bigbank.game.data.model.Task;
import com.bigbank.game.data.model.TaskStatistics;
import com.bigbank.game.data.repository.InitialStatusRepository;
import com.bigbank.game.data.repository.TaskRepository;
import com.bigbank.game.mock.MockJsonMapper;
import com.bigbank.game.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GameApplication.class})
@Transactional
@AutoConfigureTestEntityManager
@Import(TestConfig.class)
public class TaskServiceImplIT {

    @Autowired
    private TaskService taskService;
    @Autowired
    private MockJsonMapper<MessageDto> messageJsonMapper;
    @Autowired
    private MockJsonMapper<InitialStatusDto> initialStatusJsonMapper;
    @Autowired
    private MockJsonMapper<MessageStatisticsDto> messageStatisticsJsonMapper;
    @Autowired
    private InitialStatusRepository initialStatusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @MockBean
    private GameApiClient client;

    @Test
    public void givenStartGameInitialisation_whenStartGame_thenInitialGameDataIsPersisted() {
        given(client.callStartGame()).willReturn(
                initialStatusJsonMapper.mockResponse("initial-status.json", InitialStatusDto.class));

        final String gameId = taskService.startGame();

        final InitialStatus initialStatus = initialStatusRepository.findByGameId(gameId);
        assertThat(gameId).isEqualTo("gameId");
        assertThat(initialStatus).isNotNull();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/task-schema.sql", "/sql/initial-status-schema.sql"})
    public void givenListOfTasks_whenSelectEasiestTask_thenTaskWithLowestDifficultyIsReturned() {
        final Task task = taskService.selectEasiestTask("gameId");

        assertThat(task.getAdId()).isEqualTo("adId2");
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/task-schema.sql", "/sql/initial-status-schema.sql"})
    public void givenListOfTasks_whenSelectBiggestScoreHavingTask_thenTaskWithBiggestScoreIsReturned() {
        final Task task = taskService.selectBiggestScoreHavingTask("gameId");

        assertThat(task.getAdId()).isEqualTo("adId3");
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/task-schema-all-completed.sql", "/sql/initial-status-schema.sql"})
    public void givenListOfTasks_whenSuitableTaskIsNotFound_thenNewTasksAreFetchedAndReturnedBiggestScoreHavingTask() {
        given(client.callGetMessages("gameId")).willReturn(
                messageJsonMapper.mockListResponse("messages.json", MessageDto.class));

        final Task task = taskService.selectBiggestScoreHavingTask("gameId");
        assertThat(task.getAdId()).isEqualTo("8j02uxQD");
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/task-schema.sql", "/sql/initial-status-schema.sql"})
    public void givenSolveMessageApiReturnsSuccess_whenSolveTask_thenTaskMarkedAsSolved() {
        given(client.solveTask("gameId", "adId3")).willReturn(
                messageStatisticsJsonMapper.mockResponseEntity(
                        "message-statistics.json", MessageStatisticsDto.class, HttpStatus.OK));

        final TaskStatistics taskStatistics = taskService.solveTask("gameId", "adId3");

        final InitialStatus initialStatus = initialStatusRepository.findByGameId("gameId");
        final Task task = taskRepository.findByAdId("adId3");
        assertThat(task.isSolved()).isEqualTo(true);
        assertThat(taskStatistics.getTurn()).isEqualTo(1);
        assertThat(taskStatistics.getGold()).isEqualTo(10);
        assertThat(taskStatistics.getScore()).isEqualTo(10);
        assertThat(initialStatus).isNotNull();
        assertThat(initialStatus.getTurn()).isEqualTo(1);
    }

}
