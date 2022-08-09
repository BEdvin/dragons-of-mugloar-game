package com.bigbank.game.data.repository;

import com.bigbank.game.data.model.InitialStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitialStatusRepository extends CrudRepository<InitialStatus, String> {

    InitialStatus findByGameId(String gameId);
}
