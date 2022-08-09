package com.bigbank.game.service.impl;

import com.bigbank.game.GameApplication;
import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.config.TestConfig;
import com.bigbank.game.data.dto.ItemStatisticsDto;
import com.bigbank.game.data.model.InitialStatus;
import com.bigbank.game.data.model.Item;
import com.bigbank.game.data.model.TaskStatistics;
import com.bigbank.game.data.repository.InitialStatusRepository;
import com.bigbank.game.data.repository.ItemRepository;
import com.bigbank.game.mock.MockJsonMapper;
import com.bigbank.game.service.ShoppingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
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
public class ShoppingServiceImplIT {

    @Value("${game.settings.minLives}")
    private int minLives;

    @Autowired
    private ShoppingService shoppingService;
    @Autowired
    private MockJsonMapper<TaskStatistics> taskStatisticsJsonMapper;
    @Autowired
    private MockJsonMapper<ItemStatisticsDto> itemStatisticsJsonMapper;
    @Autowired
    private InitialStatusRepository initialStatusRepository;
    @Autowired
    private ItemRepository itemRepository;
    @MockBean
    private GameApiClient client;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/item-schema.sql", "/sql/initial-status-schema.sql"})
    public void givenEnoughLives_whenBuyHealingPotionIfNeeded_thenBuyingIsSkippedAndTurnIsNotIncreased() {
        final TaskStatistics taskStatistics = taskStatisticsJsonMapper
                .mockResponse("message-statistics.json", TaskStatistics.class);

        shoppingService.buyHealingPotionIfNeeded("gameId", taskStatistics);

        final InitialStatus initialStatus = initialStatusRepository.findByGameId("gameId");
        assertThat(initialStatus.getTurn()).isEqualTo(0);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/item-schema.sql", "/sql/initial-status-schema.sql"})
    public void givenNotEnoughLives_whenBuyHealingPotionIfNeeded_thenItemIsBoughtSuccessfullyAndTurnIsIncreased() {
        given(client.buyItem("gameId", "hpot")).willReturn(
                itemStatisticsJsonMapper.mockResponse("item-statistics.json", ItemStatisticsDto.class));
        final TaskStatistics taskStatistics = taskStatisticsJsonMapper
                .mockResponse("message-statistics.json", TaskStatistics.class);
        taskStatistics.setLives(minLives);
        taskStatistics.setGold(50);

        shoppingService.buyHealingPotionIfNeeded("gameId", taskStatistics);

        final InitialStatus initialStatus = initialStatusRepository.findByGameId("gameId");
        assertThat(initialStatus.getTurn()).isEqualTo(2);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/item-schema.sql", "/sql/initial-status-schema.sql"})
    public void givenNotEnoughLivesAndNotEnoughGold_whenBuyHealingPotionIfNeeded_thenBuyingIsSkippedAndTurnIsNotIncreased() {
        final TaskStatistics taskStatistics = taskStatisticsJsonMapper
                .mockResponse("message-statistics.json", TaskStatistics.class);
        taskStatistics.setLives(minLives);
        taskStatistics.setGold(49);

        shoppingService.buyHealingPotionIfNeeded("gameId", taskStatistics);

        final InitialStatus initialStatus = initialStatusRepository.findByGameId("gameId");
        assertThat(initialStatus.getTurn()).isEqualTo(0);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/item-schema.sql", "/sql/initial-status-schema.sql"})
    public void givenGoldForOneItemOf100Worth_whenBuyItem_thenBuyingIsSkippedBecauseWillBeNotEnoughForNextHealing() {
        final TaskStatistics taskStatistics = taskStatisticsJsonMapper
                .mockResponse("message-statistics.json", TaskStatistics.class);
        taskStatistics.setGold(149);

        shoppingService.buyArtefactIfHaveGold("gameId", taskStatistics);

        final InitialStatus initialStatus = initialStatusRepository.findByGameId("gameId");
        final List<Item> items = itemRepository.findAllByAvailableIsFalse();
        assertThat(items.size()).isEqualTo(0);
        assertThat(initialStatus.getTurn()).isEqualTo(0);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {"/sql/item-schema.sql", "/sql/initial-status-schema.sql"})
    public void givenGoldForMoreThanOneItemOf100Worth_whenBuyItem_thenBuyingIsSucceeded() {
        given(client.buyItem("gameId", "cs")).willReturn(
                itemStatisticsJsonMapper.mockResponse("item-statistics.json", ItemStatisticsDto.class));
        final TaskStatistics taskStatistics = taskStatisticsJsonMapper
                .mockResponse("message-statistics.json", TaskStatistics.class);
        taskStatistics.setGold(200);

        shoppingService.buyArtefactIfHaveGold("gameId", taskStatistics);

        final InitialStatus initialStatus = initialStatusRepository.findByGameId("gameId");
        final List<Item> items = itemRepository.findAllByAvailableIsFalse();
        assertThat(items.size()).isEqualTo(1);
        assertThat(initialStatus.getTurn()).isEqualTo(2);
    }
}
