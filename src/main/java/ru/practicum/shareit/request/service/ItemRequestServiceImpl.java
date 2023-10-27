package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.dto.ItemRequestDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.tool.ItemMapper;
import ru.practicum.shareit.request.exception.PaginationParamException;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.dto.ItemRequestSendingDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.tool.ItemRequestMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestCreationDto create(ItemRequestCreationDto itemRequestCreationDto, Long requesterId) {
        itemRequestCreationDto.setCreated(LocalDateTime.now());
        Optional<User> optionalUser = userRepository.findById(requesterId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(requesterId);
        }
        User user = optionalUser.get();
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestCreationDto(itemRequestCreationDto, user);
        return ItemRequestMapper.toItemRequestCreationDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestSendingDto> getRequests(Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new UserNotFoundException(requesterId);
        }
        Map<Long, ItemRequestSendingDto> requestsMap = itemRequestRepository.findAllByRequesterId(requesterId)
                .stream()
                .map(ItemRequestMapper::toItemRequestSendingDto)
                .collect(Collectors.toMap(ItemRequestSendingDto::getId, request -> request));
        pushItemsToRequests(requestsMap);
        return requestsMap.values();
    }

    private void pushItemsToRequests(Map<Long, ItemRequestSendingDto> requestsMap) {
        Map<Long, List<ItemRequestDto>> requestIdItems = itemRepository.findByRequestIdIn(requestsMap.keySet())
                .stream()
                .map(ItemMapper::toItemRequestDto)
                .collect(Collectors.groupingBy(ItemRequestDto::getRequestId));
        for (ItemRequestSendingDto request : requestsMap.values()) {
            request.setItems(requestIdItems.get(
                    request.getId()) == null ? Collections.emptyList() : requestIdItems.get(request.getId()));
        }
    }

    @Override
    public Collection<ItemRequestSendingDto> getRequests(int from, int size, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        if (from < 0 || size <= 0) {
            throw new PaginationParamException("Params size and from cannot be <= 0.");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> requests = itemRequestRepository.findAll(
                userId, page)
                .stream()
                .collect(Collectors.toList());
        Map<Long, ItemRequestSendingDto> requestsMap = requests
                .stream()
                .map(ItemRequestMapper::toItemRequestSendingDto)
                .collect(Collectors.toMap(ItemRequestSendingDto::getId, request -> request));
        pushItemsToRequests(requestsMap);
        return requestsMap.values();
    }

    @Override
    public ItemRequestSendingDto getRequest(Long requestId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(requestId);
        if (optionalItemRequest.isEmpty()) {
            throw new RequestNotFoundException(requestId);
        }
        ItemRequestSendingDto request = ItemRequestMapper.toItemRequestSendingDto(optionalItemRequest.get());
        List<ItemRequestDto> items = itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemRequestDto)
                .collect(Collectors.toList());
        request.setItems(items);
        return request;
    }
}


