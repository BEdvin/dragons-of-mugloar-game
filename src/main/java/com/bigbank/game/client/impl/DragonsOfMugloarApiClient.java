package com.bigbank.game.client.impl;

import com.bigbank.game.client.GameApiClient;
import com.bigbank.game.data.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DragonsOfMugloarApiClient implements GameApiClient {

    private final static String PARAM_GAME_ID = "gameId";
    private final static String PARAM_AD_ID = "adId";
    private final static String URI_START_GAME = "/api/v2/game/start";
    private final static String URI_MESSAGE = "/api/v2/{" + PARAM_GAME_ID + "}/messages";
    private final static String URI_SOLVE_MESSAGE = "/api/v2/{" + PARAM_GAME_ID + "}/solve/{" + PARAM_AD_ID + "}";
    private final static String URI_ITEM = "/api/v2/{" + PARAM_GAME_ID + "}/shop";
    private final static String URI_BUY_ITEM = "/api/v2/{" + PARAM_GAME_ID + "}/shop/buy/{" + PARAM_AD_ID + "}";
    private final static String URI_REPUTATION = "/api/v2/{" + PARAM_GAME_ID + "}/investigate/reputation";

    private final RestTemplate restTemplate;

    @Override
    public InitialStatusDto callStartGame() {
        return restTemplate.postForObject(URI_START_GAME, null, InitialStatusDto.class);
    }

    @Override
    public MessageDto[] callGetMessages(final String gameId) {
        return restTemplate.getForObject(URI_MESSAGE, MessageDto[].class, Map.of(PARAM_GAME_ID, gameId));
    }

    @Override
    public ItemDto[] callGetItems(final String gameId) {
        return restTemplate.getForObject(URI_ITEM, ItemDto[].class, Map.of(PARAM_GAME_ID, gameId));
    }

    @Override
    public ResponseEntity<MessageStatisticsDto> solveTask(final String gameId, final String adId) {
        try {
            return restTemplate.postForEntity(URI_SOLVE_MESSAGE, null, MessageStatisticsDto.class,
                    Map.of(PARAM_GAME_ID, gameId, PARAM_AD_ID, adId));
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(HttpStatus.valueOf(e.getRawStatusCode()));
        }

    }

    @Override
    public ItemStatisticsDto buyItem(final String gameId, final String itemId) {
        return restTemplate.postForObject(URI_BUY_ITEM, null, ItemStatisticsDto.class,
                Map.of(PARAM_GAME_ID, gameId, PARAM_AD_ID, itemId));
    }

    @Override
    public ReputationDto investigateReputation(final String gameId) {
        return restTemplate.postForObject(URI_REPUTATION, null, ReputationDto.class,
                Map.of(PARAM_GAME_ID, gameId));
    }
}
