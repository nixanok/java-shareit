package ru.practicum.shareit.request.tool;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.dto.ItemRequestSendingDto;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {

    public static ItemRequest fromItemRequestCreationDto(final ItemRequestCreationDto requestCreationDto,
                                                         final User requester) {
        return ItemRequest.builder()
                .description(requestCreationDto.getDescription())
                .created(requestCreationDto.getCreated())
                .requester(requester)
                .build();
    }

    public static ItemRequestCreationDto toItemRequestCreationDto(final ItemRequest itemRequest) {
        return ItemRequestCreationDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requesterId(itemRequest.getRequester() == null ? null : itemRequest.getRequester().getId())
                .build();
    }

    public static ItemRequestSendingDto toItemRequestSendingDto(final ItemRequest itemRequest) {
        return ItemRequestSendingDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
