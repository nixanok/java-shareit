package ru.practicum.shareit.request.tool;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.dto.ItemRequestSendingDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest fromItemRequestCreationDto(final ItemRequestCreationDto requestCreationDto,
                                                         final User requester) {
        return ItemRequest.builder()
                .description(requestCreationDto.getDescription())
                .created(requestCreationDto.getCreated())
                .requester(requester)
                .build();
    }

    public ItemRequestCreationDto toItemRequestCreationDto(final ItemRequest itemRequest) {
        return ItemRequestCreationDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requesterId(itemRequest.getRequester() == null ? null : itemRequest.getRequester().getId())
                .build();
    }

    public ItemRequestSendingDto toItemRequestSendingDto(final ItemRequest itemRequest) {
        return ItemRequestSendingDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
