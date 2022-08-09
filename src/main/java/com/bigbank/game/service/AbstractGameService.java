package com.bigbank.game.service;

import com.bigbank.game.data.model.InitialStatus;
import com.bigbank.game.data.repository.InitialStatusRepository;

public abstract class AbstractGameService {

    protected abstract InitialStatusRepository getStatusRepository();

    protected int increaseTurn(final String gameId) {
        final InitialStatus initialStatus = getStatusRepository().findByGameId(gameId);
        final int turn = initialStatus.getTurn() + 1;
        initialStatus.setTurn(turn);
        return turn;
    }

    protected void resetTurn(final InitialStatus initialStatus) {
        initialStatus.setTurn(0);
    }
}
