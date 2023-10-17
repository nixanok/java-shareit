package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingStatusException;
import ru.practicum.shareit.booking.exception.BookingTimeException;
import ru.practicum.shareit.booking.exception.OwnerNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.dto.BookingSendingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.tool.BookingMapper;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingSendingDto create(BookingCreationDto bookingCreationDto, Long brokerId) {
        if (!bookingCreationDto.getStart().isBefore(bookingCreationDto.getEnd())) {
            throw new BookingTimeException("Start is not before end");
        }
        User broker = userRepository.findById(brokerId)
                .orElseThrow(() -> new UserNotFoundException(brokerId));
        Item item = itemRepository.findById(bookingCreationDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(bookingCreationDto.getItemId()));
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(item.getId());
        }
        if (item.getOwner().getId().equals(brokerId)) {
            throw new OwnerNotFoundException();
        }
        Booking booking = BookingMapper.fromCreationDto(bookingCreationDto);
        booking.setBooker(broker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toSendingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public Booking approve(Long bookingId, Boolean isApproved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingStatusException("Booking already approved.");
        }
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new OwnerNotFoundException();
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public List<BookingSendingDto> getBookingsByBookerId(Long bookerId, State state) {
        if (!userRepository.existsById(bookerId)) {
            throw new UserNotFoundException(bookerId);
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerId(bookerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(
                                bookerId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(
                                bookerId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings =  bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                            bookerId, LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(
                        bookerId, Status.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(
                        bookerId, Status.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new IllegalArgumentException();
        }
        return bookings
                .stream()
                .map(BookingMapper::toSendingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingSendingDto> getBookingsItemsByUserId(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(
                        userId, LocalDateTime.now(),  Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(
                        userId, LocalDateTime.now(),  Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings =  bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(),  Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(
                        userId, Status.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(
                        userId, Status.REJECTED,  Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new IllegalArgumentException();
        }
        return bookings
                .stream()
                .map(BookingMapper::toSendingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingSendingDto get(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingNotFoundException(bookingId);
        }
        return BookingMapper.toSendingDto(booking);
    }

    @Override
    public List<BookingCreationDto> getAll() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingMapper::toCreationDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingCreationDto getById(Long id) {
        return BookingMapper.toCreationDto(
                bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id)));
    }

    @Override
    @Transactional
    public void removeById(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException(id);
        }
        bookingRepository.deleteById(id);
    }
}
