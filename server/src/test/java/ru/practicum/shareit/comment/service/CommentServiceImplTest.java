package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Test
    public void testPostComment() {
        Long itemId = 1L;
        Long authorId = 2L;

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .created(LocalDateTime.now().withNano(0))
                .authorName("name")
                .build();

        Item item = Item.builder()
                .id(itemId)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .build();

        User author = User.builder()
                .id(1L)
                .name("name")
                .build();

        Comment comment = new Comment();

        Mockito.when(itemRepository.findById(ArgumentMatchers.eq(itemId))).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                        ArgumentMatchers.eq(itemId), ArgumentMatchers.eq(authorId),
                        ArgumentMatchers.any(LocalDateTime.class), ArgumentMatchers.eq(Status.APPROVED)))
                .thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(ArgumentMatchers.eq(authorId))).thenReturn(Optional.of(author));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.postComment(itemId, authorId, commentDto);

        assertNotNull(result);
    }
}
