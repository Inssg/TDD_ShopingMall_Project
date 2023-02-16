package org.inssg.backend.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    //TODO: ItemRepository findAll -> findList 페이지네이션해서 조회로 변경필요
    public List<ItemResponse> getList() {
        List<Item> items = itemRepository.findAll();

        List<ItemResponse> response = items.stream()
                .map(item -> ItemResponse.toResponse(item))
                .collect(Collectors.toList());
        return response;
    }
}
