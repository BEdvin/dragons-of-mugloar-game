package com.bigbank.game.service.impl;

import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.converter.ItemStatisticsConverter;
import com.bigbank.game.data.model.Item;
import com.bigbank.game.data.model.ItemStatistics;
import com.bigbank.game.data.model.TaskStatistics;
import com.bigbank.game.data.repository.InitialStatusRepository;
import com.bigbank.game.data.repository.ItemRepository;
import com.bigbank.game.service.AbstractGameService;
import com.bigbank.game.service.ShoppingService;
import com.bigbank.game.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.bigbank.game.data.constant.GameConstants.HEALING_POTION_ID;

@Service
@RequiredArgsConstructor
public class ShoppingServiceImpl extends AbstractGameService implements ShoppingService {

    @Value("${game.settings.minLives}")
    private int minLives;
    @Value("${game.settings.itemMinPrice}")
    private int itemMinPrice;

    private final GameApiClient client;
    private final InitialStatusRepository statusRepository;
    private final ItemRepository itemRepository;
    private final ItemStatisticsConverter itemStatisticsConverter;
    private final SseService sseService;

    @Override
    public void buyHealingPotionIfNeeded(final String gameId, final TaskStatistics taskStatistics) {
        if (taskStatistics.getLives() > minLives) {
            return;
        }
        sseService.send("Healing...");
        final Item item = itemRepository.findByItemId(HEALING_POTION_ID);
        final int goldBalance = taskStatistics.getGold();
        buyItem(gameId, item, goldBalance);
    }

    @Override
    public boolean buyArtefactIfHaveGold(final String gameId, final TaskStatistics taskStatistics) {
        sseService.send("Buying artefact...");
        final Item healingItem = itemRepository.findByItemId(HEALING_POTION_ID);
        final int healingPrice = healingItem.getCost();
        final int goldBalance = taskStatistics.getGold() - healingPrice;
        if (goldBalance <= healingPrice) {
            return false;
        }
        Optional<Item> itemOptional = itemRepository
                .findFirstByCostGreaterThanAndCostLessThanEqualAndAvailableIsTrue(itemMinPrice, goldBalance);
        if (itemOptional.isPresent()) {
            final Item item = itemOptional.get();
            buyItem(gameId, item, goldBalance);
            item.setAvailable(false);
            return true;
        }
        return false;
    }

    @Override
    protected InitialStatusRepository getStatusRepository() {
        return statusRepository;
    }

    private void buyItem(final String gameId, final Item item, final int goldBalance) {
        int itemPrice = item.getCost();
        if (itemPrice > goldBalance) {
            sseService.send(String.format("Not enough gold. Current balance: %s, item price: %s",
                    goldBalance, itemPrice));
            return;
        }
        final ItemStatistics itemStatistics = itemStatisticsConverter.convert(client.buyItem(gameId, item.getItemId()));
        int turn = increaseTurn(gameId);
        sseService.send(String.format("Round turn: %s. Total turn %s. Buy item %s. Buying process %s",
                turn,
                itemStatistics.getTurn(),
                item.getName(),
                itemStatistics.isShoppingSuccess() ? "succeeded!" : "failed"));
        increaseTurn(gameId);
    }
}
