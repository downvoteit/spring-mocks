package com.downvoteit.springmocks.repository;

import com.downvoteit.springmocks.entity.Item;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
@DataJpaTest
class ItemRepositoryTest {
  private static Vacuum vacuum;
  @Autowired private ItemRepository itemRepository;

  @AfterAll
  static void teardown() {
    vacuum.cleanItemsTable();
  }

  @BeforeEach
  void config() {
    vacuum = new Vacuum();
    vacuum.cleanItemsTable();
  }

  @Test
  void save_mustReturnItem_PositiveIntegrationTest() {
    Item item = new Item(null, "A", 1, 2.00);

    assertEquals(item, itemRepository.save(item));
  }

  @Test
  void findBy_mustReturnItem_PositiveIntegrationTest() {
    assertEquals(Optional.empty(), itemRepository.findById(0));
  }

  @Test
  void findAll_mustReturnItemList_PositiveIntegrationTest() {
    assertEquals(new ArrayList<>(), itemRepository.findAll());
  }

  @Test
  void existsById_mustReturnTrue_PositiveIntegrationTest() {
    assertFalse(itemRepository.existsById(0));
  }

  @Test
  void deleteById_mustReturnVoid_PositiveIntegrationTest() {
    Item item = new Item(null, "A", 1, 2.00);

    itemRepository.save(item);

    itemRepository.deleteById(item.getId());

    assertFalse(itemRepository.existsById(item.getId()));
  }

  class Vacuum {
    void cleanItemsTable() {
      itemRepository.deleteAll();
    }
  }
}
