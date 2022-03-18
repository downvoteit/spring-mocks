package com.downvoteit.springmocks.service;

import com.downvoteit.springmocks.entity.Item;
import com.downvoteit.springmocks.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

  private final ItemRepository itemRepository;

  public ItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public Item insertItem(Item item) {
    return itemRepository.save(item);
  }

  public Item updateItem(Item item) throws ItemServiceException {
    if (item.getId() == null) throw new ItemServiceException(item.getId());

    return itemRepository.save(item);
  }

  public Item getItem(Integer id) throws ItemServiceException {
    Optional<Item> optional = itemRepository.findById(id);

    if (optional.isEmpty()) throw new ItemServiceException(id);

    return optional.get();
  }

  public List<Item> getItems() {
    return itemRepository.findAll();
  }

  public long deleteItem(Integer id) throws ItemServiceException {
    if (!itemRepository.existsById(id)) throw new ItemServiceException(id);

    itemRepository.deleteById(id);

    return id;
  }
}
