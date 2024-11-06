## Banking application service

### Author - Roman Ledenev

**____________________________________**

## Technologies used

* [Spring Boot](https://spring.io/projects/spring-boot)
* [MySQL](https://www.mysql.com/)
* [Apache Kafka](https://kafka.apache.org/)
* [Redis](https://redis.io/)
* [JSON Web Tokens](https://jwt.io/)
* [Swagger](https://swagger.io/)
* [MapStruct](https://mapstruct.org/)
* [Telegram API](https://github.com/rubenlagus/TelegramBots)
* [Flyway](https://www.red-gate.com/products/flyway/community/)
* [Docker](https://www.docker.com/)
* [Maven](https://maven.apache.org/)

**____________________________________**

## How to start?

**to run locally you can use the run.sh script** 

**or use the command from the directory with the docker-compose.yml file.**

```shell
docker-compose up --build -d
```

### Ports for start

* 8080 - bank-app main REST microservice
* 8081 - telegram-bot microservice, for showing the bank-app microservice workflow
* 8082 - notification microservice, for sending information emails
* 3306 - MySql relational database
* 6379 - Redis cache
* 2181 - Apache ZooKeeper for Apache Kafka
* 9092 - Apache Kafka message broker

**____________________________________**

## How to use?

**Launch the [telegram application](https://web.telegram.org/) on any device available to you**

**Find [@LedRomBankBot](https://t.me/ledrombankbot) and use /start command**

**You can register yourself, or use one of the existing accounts provided below**

***Note when you create an application for any new product, it will need to be approved by the Manager, 
you will need to log in under one of the existing managers to confirm, then return to your original account to continue**

### Initial users:

| Username                   | password| role    |
|----------------------------|---------|---------|
| isabella.white@yopmail.com |P@ssword1| CLIENT  |
| james.harris@yopmail.com   |P@ssword1| CLIENT  |
| mia.clark@yopmail.com      |P@ssword1| MANAGER |
| joseph.lewis@yopmail.com   |P@ssword1| CLIENT  |
| charlotte.lee@yopmail.com  |P@ssword1| CLIENT  |
| david.walker@yopmail.com   |P@ssword1| MANAGER |

**____________________________________**

## TESTS
**Integration tests are written only for the main bank-app microservice with a separate test database using**

* [H2](https://www.h2database.com/html/main.html)
* [Junit 5](https://junit.org/junit5/)
* [Mockito](https://site.mockito.org/)

**____________________________________**