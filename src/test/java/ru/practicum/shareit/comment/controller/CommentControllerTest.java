package ru.practicum.shareit.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.error.ErrorController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CommentServiceImpl commentService;

    @InjectMocks
    private CommentController bookingController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ErrorController())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPostComment() throws Exception {
        long itemId = 1L;
        long authorId = 2L;
        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .authorName("Test Author")
                .build();

        CommentDto expectedResult = CommentDto.builder()
                .text("Test Comment")
                .authorName("Test Author")
                .build();
        Mockito.when(commentService.postComment(eq(itemId), eq(authorId), any(CommentDto.class)))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(authorId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Test Comment\",\"authorName\":\"Test Author\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test Comment"))
                .andExpect(jsonPath("$.authorName").value("Test Author"));
        Mockito.verify(commentService).postComment(eq(itemId), eq(authorId), any(CommentDto.class));
    }
}
