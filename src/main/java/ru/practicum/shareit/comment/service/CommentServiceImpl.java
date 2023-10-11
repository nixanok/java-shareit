package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.exception.CommentException;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.tool.CommentMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Override
    public CommentDto postComment(Long itemId, Long authorId, CommentDto commentDto) {
        commentDto.setCreated(LocalDateTime.now());
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundException(itemId);
        }
        Item item = optionalItem.get();
        Optional<Booking> bookingOptional = bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                itemId,
                authorId,
                LocalDateTime.now(),
                Status.APPROVED);
        if (bookingOptional.isEmpty()) {
            throw new CommentException("Booking by author not found.");
        }
        Comment comment = CommentMapper.fromDto(commentDto);
        Optional<User> optionalUser = userRepository.findById(authorId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(authorId);
        }
        User user = optionalUser.get();
        comment.setAuthor(user);
        comment.setItem(item);
        return CommentMapper.toDto(commentRepository.save(comment));
    }
}
