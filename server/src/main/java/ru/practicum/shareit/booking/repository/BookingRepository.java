package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerId(Long ownerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now,
                                                              LocalDateTime now1, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start,
                                                                 LocalDateTime end, Pageable sort);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Pageable sort);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, Pageable sort);

    Page<Booking> findByItemOwnerIdAndStatus(Long ownerId, Status status, Pageable sort);

    Optional<Booking> findFirstByItemIdAndStartIsBeforeAndStatus(Long itemId, LocalDateTime start, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatus(Long itemId, LocalDateTime start, Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(Long itemId, Long userId,
                                                                 LocalDateTime end, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.start = (SELECT MAX(b2.start) FROM Booking b2 WHERE b2.item.id = b.item.id AND b2.start <= CURRENT_TIMESTAMP) " +
            "AND b.status = 'APPROVED'")
    List<Booking> findLatestBookingsForItems(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.start = (SELECT MIN(b2.start) FROM Booking b2 WHERE b2.item.id = b.item.id AND b2.start >= CURRENT_TIMESTAMP) " +
            "AND b.status = 'APPROVED'")
    List<Booking> findFutureBookingsForItems(@Param("itemIds") List<Long> itemIds);

}
