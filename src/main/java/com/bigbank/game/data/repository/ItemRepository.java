package com.bigbank.game.data.repository;

import com.bigbank.game.data.model.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, String> {

    Item findByItemId(String itemId);

    List<Item> findAllByAvailableIsFalse();

    List<Item> findAll();

    Optional<Item> findFirstByCostGreaterThanAndCostLessThanEqualAndAvailableIsTrue(int min, int max);

}
