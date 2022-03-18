package com.downvoteit.springmocks.repository;

import com.downvoteit.springmocks.entity.Item;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

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
  void testSaveIntegration() {
    Item item = new Item(null, "A", 1, 2.00);

    Assertions.assertEquals(item, itemRepository.save(item));
  }

  @Test
  void testFindByIdIntegration() {
    Assertions.assertEquals(Optional.empty(), itemRepository.findById(0));
  }

  @Test
  void testFindAllIntegration() {
    Assertions.assertEquals(new ArrayList<>(), itemRepository.findAll());
  }

  @Test
  void testExistsByIdIntegration() {
    Assertions.assertFalse(itemRepository.existsById(0));
  }

  @Test
  void testDeleteByIdIntegration() {
    Item item = new Item(null, "A", 1, 2.00);

    itemRepository.save(item);

    itemRepository.deleteById(item.getId());

    Assertions.assertFalse(itemRepository.existsById(item.getId()));
  }

  class Vacuum {
    void cleanItemsTable() {
      itemRepository.deleteAll();
    }
  }
}
