package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.dto.ItemRequestSendingDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestCreationDto create(ItemRequestCreationDto itemRequestCreationDto, Long requesterId);

    Collection<ItemRequestSendingDto> getRequests(Long requesterId);

    Collection<ItemRequestSendingDto> getRequests(int from, int size, long userId);

    ItemRequestSendingDto getRequest(Long requestId, long userId);
}
