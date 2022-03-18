package com.downvoteit.springmocks.controller;

import com.downvoteit.springmocks.entity.Item;
import com.downvoteit.springmocks.repository.ItemRepository;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsAnything;
import org.hamcrest.core.IsEqual;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {
  private static Vacuum vacuum;
  @LocalServerPort private int port;
  @Autowired private ItemRepository itemRepository;
  private RequestSpecification requestSpecification;

  @BeforeAll
  static void init() {
    // disable verbose HttpClient logging
    for (String log : new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"))) {
      ch.qos.logback.classic.Logger artLogger =
          (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(log);

      artLogger.setLevel(ch.qos.logback.classic.Level.INFO);
      artLogger.setAdditive(false);
    }
  }

  @AfterAll
  static void teardown() {
    vacuum.cleanItemsTable();
  }

  private static String convertToJson(Item item) throws JSONException {
    return new JSONObject()
        .put("id", item.getId())
        .put("name", item.getName())
        .put("amount", item.getAmount())
        .put("price", item.getPrice())
        .toString();
  }

  @BeforeEach
  void config() {
    // set common specifications
    requestSpecification =
        new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setBasePath("/items")
            .setPort(port)
            .setContentType(ContentType.JSON)
            .build();

    vacuum = new Vacuum();
    vacuum.cleanItemsTable();
  }

  @Test
  void testPositiveInsertItemIntegration() throws JSONException {
    Item item = new Item(null, "A", 1, 2.00);

    given()
        .spec(requestSpecification)
        .body(convertToJson(item))
        .when()
        .post()
        .then()
        .log()
        .body()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .body(Messages.MESSAGE.get(), equalTo(Messages.SUCCESS.get()))
        .and()
        .body(String.format("%s.name", Messages.INSERTED.get()), IsEqual.equalTo(item.getName()));
  }

  @Test
  void testPositiveUpdateItemIntegration() throws JSONException {
    Item item = itemRepository.save(new Item(null, "A", 1, 2.00));

    given()
        .spec(requestSpecification)
        .body(convertToJson(item))
        .when()
        .put()
        .then()
        .log()
        .body()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .body(Messages.MESSAGE.get(), equalTo(Messages.SUCCESS.get()))
        .and()
        .body(String.format("%s.id", Messages.UPDATED.get()), IsEqual.equalTo(item.getId()));
  }

  @Test
  void testNegativeUpdateItemIntegration() throws JSONException {
    Item item = new Item();

    given()
        .spec(requestSpecification)
        .body(convertToJson(item))
        .when()
        .put()
        .then()
        .log()
        .body()
        .assertThat()
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .body(Messages.FAILED.get(), IsAnything.anything());
  }

  @Test
  void testPositiveGetItemIntegration() {
    Item item = itemRepository.save(new Item(null, "A", 1, 2.00));

    given()
        .spec(requestSpecification)
        .when()
        .pathParams("id", item.getId())
        .get("/{id}")
        .then()
        .log()
        .body()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .body(Messages.MESSAGE.get(), equalTo(Messages.SUCCESS.get()))
        .and()
        .body(String.format("%s.id", Messages.GET.get()), IsEqual.equalTo(item.getId()));
  }

  @Test
  void testNegativeGetItemIntegration() {
    given()
        .spec(requestSpecification)
        .when()
        .pathParams("id", 0)
        .get("/{id}")
        .then()
        .log()
        .body()
        .assertThat()
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .body(Messages.FAILED.get(), IsAnything.anything());
  }

  @Test
  void testPositiveGetItemsIntegration() {
    int size = 5;
    for (int i = 0; i < size; i++) {
      itemRepository.save(new Item(null, "A" + (i + 1), 1, 2.00));
    }

    given()
        .spec(requestSpecification)
        .when()
        .get()
        .then()
        .log()
        .body()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .body(Messages.MESSAGE.get(), equalTo(Messages.SUCCESS.get()))
        .and()
        .body(String.format("%s.size()", Messages.GET.get()), Is.is(size));
  }

  @Test
  void testPositiveDeleteItemIntegration() {
    Item item = itemRepository.save(new Item(null, "A", 1, 2.00));

    given()
        .spec(requestSpecification)
        .when()
        .pathParams("id", item.getId())
        .delete("/{id}")
        .then()
        .log()
        .body()
        .assertThat()
        .statusCode(HttpStatus.OK.value())
        .body(Messages.MESSAGE.get(), equalTo(Messages.SUCCESS.get()))
        .and()
        .body(Messages.DELETED.get(), IsEqual.equalTo(item.getId()));
  }

  @Test
  void testNegativeDeleteItemIntegration() {
    given()
        .spec(requestSpecification)
        .when()
        .pathParams("id", 0)
        .delete("/{id}")
        .then()
        .log()
        .body()
        .assertThat()
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
        .body(Messages.FAILED.get(), IsAnything.anything());
  }

  // clear items table before each test
  class Vacuum {
    void cleanItemsTable() {
      itemRepository.deleteAll();
    }
  }
}
