package com.bigbank.game.converter;

import com.bigbank.game.data.dto.ItemDto;
import com.bigbank.game.data.model.Item;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ItemConverter extends AbstractConverter<ItemDto, Item> {

    @Override
    public Item convert(final ItemDto itemDto) {
        return Optional.ofNullable(itemDto)
                .map(i -> Item.builder()
                        .itemId(i.getId())
                        .name(i.getName())
                        .cost(i.getCost())
                        .available(true)
                        .build())
                .orElse(null);
    }
}
