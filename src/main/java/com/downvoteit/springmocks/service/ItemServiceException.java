package com.downvoteit.springmocks.service;

public class ItemServiceException extends Exception {
  private static final String template = "item by id '%d' is not found";

  public ItemServiceException(Integer message) {
    super(String.format(template, message));
  }
}
