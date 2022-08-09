package com.bigbank.game.service.impl;

import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.converter.InitialStatusConverter;
import com.bigbank.game.converter.TaskStatisticsConverter;
import com.bigbank.game.data.dto.InitialStatusDto;
import com.bigbank.game.data.dto.MessageStatisticsDto;
import com.bigbank.game.data.model.InitialStatus;
import com.bigbank.game.data.model.Task;
import com.bigbank.game.data.model.TaskStatistics;
import com.bigbank.game.data.repository.InitialStatusRepository;
import com.bigbank.game.data.repository.TaskRepository;
import com.bigbank.game.service.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private InitialStatusRepository initialStatusRepository;
    @Mock
    private SseService sseService;
    @Mock
    private GameApiClient client;
    @Mock
    private InitialStatusConverter initialStatusConverter;
    @Mock
    private TaskStatisticsConverter taskStatisticsConverter;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskServiceImpl taskService;

    private InitialStatus initialStatus;
    private InitialStatusDto initialStatusDto;
    private ResponseEntity<MessageStatisticsDto> responseEntity;
    private TaskStatistics taskStatistics;
    private Task task;

    @BeforeEach
    public void setup() {
        initialStatus = getInitialStatus();
        initialStatusDto = getInitialStatusDto();
        responseEntity = getResponseEntity(HttpStatus.OK);
        taskStatistics = getTaskStatistics();
        task = getTask();
    }

    @Test
    public void givenStartGameInitialisation_whenStartGame_thenInitialGameDataIsSavedToDb() {
        given(client.callStartGame()).willReturn(initialStatusDto);
        given(initialStatusRepository.save(initialStatus)).willReturn(initialStatus);
        given(initialStatusConverter.convert(initialStatusDto)).willReturn(initialStatus);
        doNothing().when(sseService).send(isA(String.class));

        final String gameId = taskService.startGame();

        assertThat(gameId).isEqualTo("gameId");
        verify(sseService, times(1)).send(anyString());
    }

    @Test
    public void givenGameIdAndTaskId_whenApiReturnsSucceeded_thenReturnTaskStatisticMarkedAsSucceeded() {
        given(client.solveTask(anyString(), anyString())).willReturn(responseEntity);
        given(taskStatisticsConverter.convert(responseEntity.getBody())).willReturn(taskStatistics);
        given(taskRepository.findByAdId("adId")).willReturn(task);
        given(initialStatusRepository.findByGameId("gameId")).willReturn(initialStatus);
        doNothing().when(sseService).send(isA(String.class));

        final TaskStatistics taskStatistics = taskService.solveTask("gameId", "adId");

        assertThat(taskStatistics.isSuccess()).isEqualTo(true);
        assertThat(taskStatistics.isSkipped()).isEqualTo(false);
        assertThat(taskStatistics.getTurn()).isEqualTo(1);
        verify(sseService, times(1)).send(anyString());
    }

    @Test
    public void givenGameIdAndTaskId_whenApiReturnsFailed_thenReturnTaskStatisticMarkedAsFailed() {
        Objects.requireNonNull(responseEntity.getBody()).setSuccess(false);
        Objects.requireNonNull(taskStatistics).setSuccess(false);
        given(client.solveTask(anyString(), anyString())).willReturn(responseEntity);
        given(taskStatisticsConverter.convert(responseEntity.getBody())).willReturn(taskStatistics);
        given(taskRepository.findByAdId("adId")).willReturn(task);
        given(initialStatusRepository.findByGameId("gameId")).willReturn(initialStatus);
        doNothing().when(sseService).send(isA(String.class));

        final TaskStatistics taskStatistics = taskService.solveTask("gameId", "adId");

        assertThat(taskStatistics.isSuccess()).isEqualTo(false);
        assertThat(taskStatistics.isSkipped()).isEqualTo(false);
        verify(sseService, times(1)).send(anyString());
    }

    @Test
    public void givenGameIdAndTaskId_whenApiReturnsError_thenReturnStatisticsWithErrorStatusCode() {
        responseEntity = getResponseEntity(HttpStatus.BAD_REQUEST);
        given(client.solveTask(anyString(), anyString())).willReturn(responseEntity);
        given(taskRepository.findByAdId("adId")).willReturn(task);
        doNothing().when(sseService).send(isA(String.class));

        final TaskStatistics taskStatistics = taskService.solveTask("gameId", "adId");

        assertThat(taskStatistics.isSuccess()).isEqualTo(false);
        assertThat(taskStatistics.isSkipped()).isEqualTo(true);
        verify(sseService, times(2)).send(anyString());
    }

    private InitialStatus getInitialStatus() {
        return InitialStatus.builder()
                .id(UUID.randomUUID().toString())
                .level(0)
                .gold(0)
                .lives(3)
                .gameId("gameId")
                .highScore(0)
                .turn(0)
                .build();
    }

    private InitialStatusDto getInitialStatusDto() {
        return InitialStatusDto.builder()
                .level(0)
                .gold(0)
                .lives(3)
                .gameId("gameId")
                .highScore(0)
                .turn(0)
                .build();
    }

    private ResponseEntity<MessageStatisticsDto> getResponseEntity(final HttpStatus statusCode) {
        final MessageStatisticsDto messageStatisticsDto = MessageStatisticsDto.builder()
                .message("Message to solve")
                .gold(50)
                .lives(3)
                .score(80)
                .success(true)
                .turn(1)
                .highScore(0)
                .build();
        return new ResponseEntity<>(messageStatisticsDto, statusCode);
    }

    private TaskStatistics getTaskStatistics() {
        return TaskStatistics.builder()
                .message("Message to solve")
                .gold(50)
                .lives(3)
                .score(80)
                .success(true)
                .turn(1)
                .skipped(false)
                .highScore(0)
                .build();
    }

    private Task getTask() {
        return Task.builder()
                .adId("adId")
                .message("Message to solve")
                .difficulty(1)
                .expiresIn(7)
                .reward(80)
                .solved(false)
                .build();
    }
}
