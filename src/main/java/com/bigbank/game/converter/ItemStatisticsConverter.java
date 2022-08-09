package com.bigbank.game.converter;

import com.bigbank.game.data.dto.ItemStatisticsDto;
import com.bigbank.game.data.model.ItemStatistics;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ItemStatisticsConverter extends AbstractConverter<ItemStatisticsDto, ItemStatistics> {

    @Override
    public ItemStatistics convert(final ItemStatisticsDto itemStatisticsDto) {
        return Optional.ofNullable(itemStatisticsDto)
                .map(i -> ItemStatistics.builder()
                        .shoppingSuccess(i.isShoppingSuccess())
                        .gold(i.getGold())
                        .lives(i.getLives())
                        .level(i.getLevel())
                        .turn(i.getTurn())
                        .build())
                .orElse(null);
    }
}
