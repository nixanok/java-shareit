package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(Long requesterId);

    @Query("SELECT r FROM ItemRequest r " +
           "WHERE NOT (r.requester.id = :userId) " +
           "ORDER BY r.created")
    Page<ItemRequest> findAll(@Param("userId") Long userId, Pageable pageable);

}
