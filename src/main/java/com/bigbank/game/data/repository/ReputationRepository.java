package com.bigbank.game.data.repository;

import com.bigbank.game.data.model.Reputation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReputationRepository extends CrudRepository<Reputation, String> {

    List<Reputation> findAll();

}
