package com.downvoteit.springmocks.service;

import com.downvoteit.springmocks.entity.Item;
import com.downvoteit.springmocks.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
  @Mock Item item;
  @Mock List<Item> items = new ArrayList<>();
  @Mock ItemRepository itemRepository;

  @InjectMocks ItemService itemService;

  @Test
  void insertItem_mustSaveItem_PositiveTest() {
    when(itemRepository.save(item)).thenReturn(item);

    assertEquals(item, itemService.insertItem(item));
  }

  @Test
  void updateItem_mustReturnItem_PositiveTest() throws ItemServiceException {
    when(itemRepository.save(item)).thenReturn(item);

    assertEquals(item, itemService.updateItem(item));
  }

  @Test
  void getItem_mustReturnItem_PositiveTest() throws ItemServiceException {
    int id = 0;

    when(itemRepository.findById(id)).thenReturn(Optional.of(item));

    assertEquals(item, itemService.getItem(id));
  }

  @Test
  void getItems_mustReturnItemList_PositiveTest() {
    when(itemRepository.findAll()).thenReturn(items);

    assertEquals(items, itemService.getItems());
  }

  @Test
  void deleteById_mustReturnVoid_PositiveTest() throws ItemServiceException {
    int id = 0;

    when(itemRepository.existsById(0)).thenReturn(true);

    doNothing().when(itemRepository).deleteById(id);
    itemRepository.deleteById(id);

    verify(itemRepository, times(1)).deleteById(id);

    assertEquals(id, itemService.deleteItem(id));
  }

  @Test
  void deleteItem_mustThrow_NegativeTest() {
    int id = 0;

    when(itemRepository.existsById(id)).thenReturn(false);

    Exception exception =
        assertThrows(ItemServiceException.class, () -> itemService.deleteItem(id));

    assertTrue(exception.getMessage().contains("item by id '0' is not found"));
  }
}
