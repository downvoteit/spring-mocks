# spring-mocks

## Purpose

- Create a simple REST API web application with Spring Boot and PostgreSQL
- Apply JUnit, Mockito and RestAssured in testing 

## Learning resources

- JUnit 5 docs
  - https://junit.org/junit5/docs/current/user-guide
- Mockito docs
  - https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- RestAssured docs
  - https://www.javadoc.io/doc/io.rest-assured/rest-assured/latest/io/restassured/RestAssured.html

## Additional VM options for RestAssured integration tests

### Description

- Removes this warning: `WARNING: An illegal reflective access operation has occurred` 
- Should not be used in production due to associated security risks

### Additional information

- `-ea` flag enables assertion on all application classes
- `--add-opens` programmatically opens java.base module for unsafe manipulations

### VM options

```
-ea
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED
```
