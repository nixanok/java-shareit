package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * " +
                   "from items i " +
                   "where (i.name ilike concat('%', ?1, '%') or i.description ilike concat('%', ?1, '%')) and i.available = true",
            nativeQuery = true)
    List<Item> findAllByNameAndDescription(String text);

    List<Item> findAllByOwnerId(Long ownerId);
}
