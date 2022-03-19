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

## Necessary program options

### Run using the `dev` profile

- Put this into `Environment variables` of your target Intellij Run/Debug Configuration

```
spring.profiles.active=dev
```

### Run using the `test` profile

- Put this into `Environment variables` of your target Intellij Run/Debug Configuration

```
spring.profiles.active=test
```

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

# Flyway migration using Maven

## Steps

### 1. Add a Maven dependency to migrate a schema on application startup

```
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
  <version>8.5.4</version>
</dependency>
```

### 2. Add a Maven plugin for other administrative tasks

```
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>8.5.4</version>
</plugin>
```

### 2. Create necessary configuration files, and the version logs for the SQL

- Create `flyway.conf` in the root of the project

```
flyway.user=<user>
flyway.password=<password>
flyway.schemas=<schema>
flyway.url=<url>
flyway.locations=classpath:db/versions
```

- Add Flyway configurations into `application-dev.yaml`

```
...
flyway:
    enabled: true
    locations: classpath:db/versions
    url: <url>
    schemas: <schema>
    user: <user>
    password: <password>
...
```

- Create `db/versions` folder with necessary SQL in resources of the application

- Must SQL migration file names must adhere to `<prefix><version number>__<version descriptor>.sql` format

- Supported prefixes are `V` for version, `U` for undo, and `R` for repeated migrations

- More can be found here
  - https://flywaydb.org/documentation/concepts/migrations

### 3. Commands (all commands are idempotent)

```
# Print applied migrations
mvn flyway:info -Dflyway.configFiles=flyway.conf

# Validate the migrations
mvn flyway:validate -Dflyway.configFiles=flyway.conf

# Apply all scheduled migrations
mvn flyway:migrate -Dflyway.configFiles=flyway.conf

# Undo last migration (for premium users)
mvn flyway:undo -Dflyway.configFiles=flyway.conf

# Set a new baseline migration
mvn flyway:baseline -Dflyway.configFiles=flyway.conf

# Repair any broken migrations
mvn flyway:repair -Dflyway.configFiles=flyway.conf

# Undo all migrations
mvn flyway:clean -Dflyway.configFiles=flyway.conf
```
