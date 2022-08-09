package com.bigbank.game.converter;

import com.bigbank.game.data.dto.ReputationDto;
import com.bigbank.game.data.model.Reputation;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReputationConverter extends AbstractConverter<ReputationDto, Reputation> {

    @Override
    public Reputation convert(final ReputationDto reputationDto) {
        return Optional.ofNullable(reputationDto)
                .map(r -> Reputation.builder()
                        .people(r.getPeople())
                        .state(r.getState())
                        .underworld(r.getUnderworld())
                        .build())
                .orElse(null);
    }
}
