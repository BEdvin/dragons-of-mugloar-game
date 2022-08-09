package com.bigbank.game.data.repository;

import com.bigbank.game.data.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, String> {

    Task findByAdId(String adId);

    List<Task> findAll();

}
