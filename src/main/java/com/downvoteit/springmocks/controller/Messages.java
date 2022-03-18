package com.downvoteit.springmocks.controller;

public enum Messages {
  // states
  INSERTED("inserted"),
  UPDATED("updated"),
  GET("got"),
  DELETED("deleted"),
  SUCCESS("success"),
  FAILED("failed"),
  // misc
  MESSAGE("message");

  private final String name;

  Messages(String message) {
    this.name = message;
  }

  public String get() {
    return name;
  }
}
