package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.dto.ItemRequestSendingDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestCreationDto createRequest(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long requesterId,
                                                @RequestBody @Valid ItemRequestCreationDto itemRequestCreationDto) {
        log.debug("Request \"createRequest\"is called.");
        return itemRequestService.create(itemRequestCreationDto, requesterId);
    }

    @GetMapping
    public Collection<ItemRequestSendingDto> getRequestsByRequester(
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long requesterId) {
        log.debug("Request \"getRequestsByRequester\"is called.");
        return itemRequestService.getRequests(requesterId);
    }

    @GetMapping(path = "/all")
    public Collection<ItemRequestSendingDto> getRequests(
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Request \"getRequests\"is called.");
        return itemRequestService.getRequests(from, size, userId);
    }

    @GetMapping(path = "/{requestId}")
    public ItemRequestSendingDto getRequest(
            @PositiveOrZero @PathVariable(name = "requestId") long requestId,
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Request \"getRequest\"is called.");
        return itemRequestService.getRequest(requestId, userId);
    }
}
