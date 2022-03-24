# spring-mocks

## Purpose

- Create a simple REST API web application with Spring Boot and PostgreSQL
- Apply JUnit, Mockito and RestAssured in testing
- Apply Database migration using Flyway
- Initiate containerization and upload an image to a remote Docker registry using Google Jib 

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

# Flyway migration with Maven

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

### 3. Test by running commands below (all commands are idempotent)

```
# Print applied migrations
mvn flyway:info -Dflyway.configFiles=flyway.conf

# Validate the migrations
mvn flyway:validate -Dflyway.configFiles=flyway.conf

# Apply all scheduled migrations
mvn flyway:migrate -Dflyway.configFiles=flyway.conf

# Undo last migration (for premium users)
mvn flyway:undo -Dflyway.configFiles=flyway.conf

# Sets a new baseline migration version as a start for an existing database
mvn flyway:baseline -Dflyway.configFiles=flyway.conf

# Repair any broken migrations in the metadata table
mvn flyway:repair -Dflyway.configFiles=flyway.conf

# Undo all migrations
mvn flyway:clean -Dflyway.configFiles=flyway.conf
```

# Google Jib integration with Maven

## Steps

### 1. Create an account on Docker Hub (https://hub.docker.com/) and add the plugin

- Login into Docker Hub on your local machine (follow Docker credential procedures)

```
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>3.2.0</version>
    <configuration>
        ...
    </configuration>
</plugin>
```

### 2. Add necessary configurations to the plugin

- Examples configurations are: to/from image, container properties

```
<configuration>
  <from>
    <image>openjdk:11.0.14-jre@sha256:e2e90ec68d3eee5a526603a3160de353a178c80b05926f83d2f77db1d3440826</image>
  </from>
  <to>
    <image>registry.hub.docker.com/downvoteit/${project.artifactId}:${project.version}</image>
    <tags>
      <tag>${project.version}</tag>
    </tags>
  </to>
  <container>
    <creationTime>USE_CURRENT_TIMESTAMP</creationTime>
    <mainClass>com.downvoteit.springmocks.SpringMocksApplication</mainClass>
    <user>nobody</user>
    <ports>
      <port>6002</port>
    </ports>
  </container>
</configuration>
```

### 3. Add Maven lifecycle/execution options

```
<executions>
  <execution>
    <phase>package</phase>
    <goals>
      <goal>build</goal>
    </goals>
  </execution>
</executions>
```

### 4. Test by running the commands outlined below (or use Intellij Maven UI to trigger)

- Trigger docker build after cleaning, validation and compilation 

```
mvn compile jib:build
```

- Trigger docker build after packaging (default configuration, and will run all tests)

```
mvn package
```

### 5. Pull the image from Docker Hub and run it using a "production" profile  

```bash
# Start all services
docker-compose --file docker-compose.prod.yaml --env-file ./env.prod up -d

# Follow webapp logs 
docker logs --follow spring_mocks_web 

# Stop all services
docker-compose --file docker-compose.prod.yaml --env-file ./env.prod down
```
