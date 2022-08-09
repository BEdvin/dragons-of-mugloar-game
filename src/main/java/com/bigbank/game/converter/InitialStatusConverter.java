package com.bigbank.game.converter;

import com.bigbank.game.data.dto.InitialStatusDto;
import com.bigbank.game.data.model.InitialStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InitialStatusConverter extends AbstractConverter<InitialStatusDto, InitialStatus> {

    @Override
    public InitialStatus convert(final InitialStatusDto initialStatusDto) {
        return Optional.ofNullable(initialStatusDto)
                .map(i -> InitialStatus.builder()
                        .gameId(i.getGameId())
                        .lives(i.getLives())
                        .gold(i.getGold())
                        .level(i.getLevel())
                        .score(i.getScore())
                        .highScore(i.getHighScore())
                        .turn(i.getTurn())
                        .build())
                .orElse(null);
    }
}
