package com.bigbank.game.client;

import com.bigbank.game.data.dto.*;
import org.springframework.http.ResponseEntity;

public interface GameApiClient {

    InitialStatusDto callStartGame();

    MessageDto[] callGetMessages(String gameId);

    ItemDto[] callGetItems(String gameId);

    ResponseEntity<MessageStatisticsDto> solveTask(String gameId, String adId);

    ItemStatisticsDto buyItem(String gameId, String itemId);

    ReputationDto investigateReputation(String gameId);
}
