package com.bigbank.game.converter;

import com.bigbank.game.data.dto.MessageStatisticsDto;
import com.bigbank.game.data.model.TaskStatistics;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskStatisticsConverter extends AbstractConverter<MessageStatisticsDto, TaskStatistics> {

    @Override
    public TaskStatistics convert(final MessageStatisticsDto messageStatisticsDto) {
        return Optional.ofNullable(messageStatisticsDto)
                .map(m -> TaskStatistics.builder()
                        .success(m.isSuccess())
                        .lives(m.getLives())
                        .gold(m.getGold())
                        .score(m.getScore())
                        .highScore(m.getHighScore())
                        .turn(m.getTurn())
                        .message(m.getMessage())
                        .build())
                .orElse(null);
    }
}
