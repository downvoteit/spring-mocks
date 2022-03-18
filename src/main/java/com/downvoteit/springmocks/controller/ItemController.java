package com.downvoteit.springmocks.controller;

import com.downvoteit.springmocks.entity.Item;
import com.downvoteit.springmocks.service.ItemService;
import com.downvoteit.springmocks.service.ItemServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(
    value = "/items",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {
  private final ItemService itemService;

  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  private static <T> Map<String, Object> createResponse(Messages action, T value) {
    Map<String, Object> response = new HashMap<>();

    // if not explicitly FAILED then must be SUCCESS
    if (action != Messages.FAILED) response.put(Messages.MESSAGE.get(), Messages.SUCCESS.get());

    response.put(action.get(), value);

    return response;
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> insertItem(@RequestBody Item item) {
    return new ResponseEntity<>(
        createResponse(Messages.INSERTED, itemService.insertItem(item)), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<Map<String, Object>> updateItem(@RequestBody Item item) {
    Map<String, Object> response;
    HttpStatus status = HttpStatus.OK;

    try {
      response = createResponse(Messages.UPDATED, itemService.updateItem(item));
    } catch (ItemServiceException e) {
      response = createResponse(Messages.FAILED, e.getMessage());
      status = HttpStatus.UNPROCESSABLE_ENTITY;

      log.error("", e);
    }

    return new ResponseEntity<>(response, status);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Map<String, Object>> getItem(@PathVariable("id") Integer id) {
    Map<String, Object> response;
    HttpStatus status = HttpStatus.OK;

    try {
      response = createResponse(Messages.GET, itemService.getItem(id));
    } catch (ItemServiceException e) {
      response = createResponse(Messages.FAILED, e.getMessage());
      status = HttpStatus.UNPROCESSABLE_ENTITY;

      log.error("", e);
    }

    return new ResponseEntity<>(response, status);
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> getItems() {
    return new ResponseEntity<>(
        createResponse(Messages.GET, itemService.getItems()), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable("id") Integer id) {
    Map<String, Object> response;
    HttpStatus status = HttpStatus.OK;

    try {
      response = createResponse(Messages.DELETED, itemService.deleteItem(id));
    } catch (ItemServiceException e) {
      response = createResponse(Messages.FAILED, e.getMessage());
      status = HttpStatus.UNPROCESSABLE_ENTITY;

      log.error("", e);
    }

    return new ResponseEntity<>(response, status);
  }
}
