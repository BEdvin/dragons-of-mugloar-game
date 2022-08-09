package com.bigbank.game.service.impl;

import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.converter.ItemStatisticsConverter;
import com.bigbank.game.data.dto.ItemStatisticsDto;
import com.bigbank.game.data.model.InitialStatus;
import com.bigbank.game.data.model.Item;
import com.bigbank.game.data.model.ItemStatistics;
import com.bigbank.game.data.model.TaskStatistics;
import com.bigbank.game.data.repository.InitialStatusRepository;
import com.bigbank.game.data.repository.ItemRepository;
import com.bigbank.game.service.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static com.bigbank.game.data.constant.GameConstants.HEALING_POTION_ID;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingServiceImplTest {

    @Mock
    private InitialStatusRepository initialStatusRepository;
    @Mock
    private SseService sseService;
    @Mock
    private GameApiClient client;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemStatisticsConverter itemStatisticsConverter;
    @InjectMocks
    private ShoppingServiceImpl shoppingService;

    private InitialStatus initialStatus;
    private TaskStatistics taskStatistics;
    private Item item;
    private ItemStatisticsDto itemStatisticsDto;
    private ItemStatistics itemStatistics;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(shoppingService, "minLives", 2);
        initialStatus = getInitialStatus();
        item = getItem();
        itemStatisticsDto = getItemStatisticsDto();
        itemStatistics = getItemStatistics();
        taskStatistics = getTaskStatistics();
    }

    @Test
    public void givenGameIdAndTaskStatistics_whenBuyHealingPotionIfNeeded_thenBuyItemApiCallIsInvoked() {
        given(initialStatusRepository.findByGameId("gameId")).willReturn(initialStatus);
        given(client.buyItem("gameId", HEALING_POTION_ID)).willReturn(itemStatisticsDto);
        given(itemRepository.findByItemId(HEALING_POTION_ID)).willReturn(item);
        given(itemStatisticsConverter.convert(itemStatisticsDto)).willReturn(itemStatistics);
        doNothing().when(sseService).send(isA(String.class));

        shoppingService.buyHealingPotionIfNeeded("gameId", taskStatistics);

        verify(client, times(1)).buyItem(anyString(), anyString());
        verify(sseService, times(2)).send(anyString());

    }

    @Test
    public void givenGameIdAndTaskStatistics_whenBuyArtefactIfHaveGold_thenBuyItemApiCallIsInvoked() {
        given(initialStatusRepository.findByGameId("gameId")).willReturn(initialStatus);
        given(client.buyItem("gameId", "itemId")).willReturn(itemStatisticsDto);
        given(itemRepository.findByItemId(HEALING_POTION_ID)).willReturn(item);
        final Item artifact = item.toBuilder().build();
        artifact.setItemId("itemId");
        given(itemRepository
                .findFirstByCostGreaterThanAndCostLessThanEqualAndAvailableIsTrue(anyInt(), anyInt()))
                .willReturn(Optional.of(artifact));
        given(itemStatisticsConverter.convert(itemStatisticsDto)).willReturn(itemStatistics);
        doNothing().when(sseService).send(isA(String.class));

        shoppingService.buyArtefactIfHaveGold("gameId", taskStatistics);

        verify(client, times(1)).buyItem(anyString(), anyString());
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

    private TaskStatistics getTaskStatistics() {
        return TaskStatistics.builder()
                .message("Message to solve")
                .gold(200)
                .lives(2)
                .score(80)
                .success(true)
                .turn(1)
                .skipped(false)
                .highScore(0)
                .build();
    }

    private Item getItem() {
        return Item.builder()
                .available(true)
                .itemId(HEALING_POTION_ID)
                .cost(50)
                .name("Item name")
                .build();
    }

    private ItemStatisticsDto getItemStatisticsDto() {
        return ItemStatisticsDto.builder()
                .level(0)
                .gold(120)
                .shoppingSuccess(true)
                .lives(3)
                .turn(1)
                .build();
    }

    private ItemStatistics getItemStatistics() {
        return ItemStatistics.builder()
                .level(0)
                .gold(120)
                .shoppingSuccess(true)
                .lives(3)
                .turn(1)
                .build();
    }
}
