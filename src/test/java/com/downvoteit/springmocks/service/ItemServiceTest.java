package com.downvoteit.springmocks.service;

import com.downvoteit.springmocks.entity.Item;
import com.downvoteit.springmocks.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
  @Mock Item item;
  @Mock List<Item> items = new ArrayList<>();
  @Mock ItemRepository itemRepository;

  @InjectMocks ItemService itemService;

  @Test
  void testInsertItem() {
    when(itemRepository.save(item)).thenReturn(item);

    Assertions.assertEquals(item, itemService.insertItem(item));
  }

  @Test
  void testUpdateItem() throws ItemServiceException {
    when(itemRepository.save(item)).thenReturn(item);

    Assertions.assertEquals(item, itemService.updateItem(item));
  }

  @Test
  void testGetItem() throws ItemServiceException {
    int id = 0;

    when(itemRepository.findById(id)).thenReturn(Optional.of(item));

    Assertions.assertEquals(item, itemService.getItem(id));
  }

  @Test
  void testGetItems() {
    when(itemRepository.findAll()).thenReturn(items);

    Assertions.assertEquals(items, itemService.getItems());
  }

  @Test
  void testDeleteItemsThrows() {
    int id = 0;

    when(itemRepository.existsById(id)).thenReturn(false);

    Exception exception =
        Assertions.assertThrows(ItemServiceException.class, () -> itemService.deleteItem(id));

    Assertions.assertTrue(exception.getMessage().contains("item by id '0' is not found"));
  }

  @Test
  void testDeleteItemsDoesNotThrow() throws ItemServiceException {
    int id = 0;

    when(itemRepository.existsById(0)).thenReturn(true);

    doNothing().when(itemRepository).deleteById(id);
    itemRepository.deleteById(id);

    verify(itemRepository, times(1)).deleteById(id);

    Assertions.assertEquals(id, itemService.deleteItem(id));
  }
}
