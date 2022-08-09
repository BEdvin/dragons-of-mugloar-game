package com.bigbank.game.converter;

import com.bigbank.game.data.dto.MessageDto;
import com.bigbank.game.data.constant.TaskProbability;
import com.bigbank.game.data.model.Task;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

import static com.bigbank.game.data.constant.TaskProbability.UNKNOWN_DIFFICULTY;

@Component
public class TaskConverter extends AbstractConverter<MessageDto, Task> {

    @Override
    public Task convert(final MessageDto messageDto) {
        return Optional.ofNullable(messageDto)
                .map(m -> Task.builder()
                        .adId(m.getAdId())
                        .message(m.getMessage())
                        .reward(m.getReward())
                        .expiresIn(m.getExpiresIn())
                        .probability(m.getProbability())
                        .difficulty(Stream.of(TaskProbability.values())
                                .filter(value -> value.getMessage().equals(m.getProbability()))
                                .findFirst()
                                .map(TaskProbability::getDifficulty)
                                .orElse(UNKNOWN_DIFFICULTY.getDifficulty()))
                        .build())
                .orElse(null);
    }
}
