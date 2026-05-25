# Spring Boot Testing Questions & Answers

# 1. How can we configure the application context when running tests with `@SpringBootTest` annotation?

We can configure the application context in several ways:

## Using Properties

```java
@SpringBootTest(
    properties = {
        "app.message=Hello Test"
    }
)
class MyTest {
}
```

This adds custom properties only for the test.

---

## Using Specific Configuration Classes

```java
@SpringBootTest(classes = MyConfig.class)
class MyTest {
}
```

This loads only the specified configuration class.

---

## Using Profiles

```java
@ActiveProfiles("test")
@SpringBootTest
class MyTest {
}
```

This loads `application-test.properties`.

---

# 2. How can you exclude auto configuration from a test?

We can exclude auto configuration using:

```java
@SpringBootTest
@EnableAutoConfiguration(
    exclude = {
        DataSourceAutoConfiguration.class
    }
)
class MyTest {
}
```

Example:

- Excluding database configuration
- Excluding security configuration
- Preventing unnecessary beans from loading

This makes tests faster and avoids unwanted dependencies.

---

# 3. How many application contexts can be cached when running tests?

By default, Spring stores up to:

```text
32 application contexts
```

in the test context cache.

---

## What happens if we increase the cache size?

### Advantages

- More contexts reused
- Faster test execution
- Less context reloading

### Disadvantages

- More memory usage
- Higher RAM consumption
- Possible slower system performance

---

## What happens if there is no caching?

Without caching:

- Spring creates a new application context for every test
- Tests become much slower
- More CPU and memory usage
- Startup time increases significantly

Caching improves test performance.

---

# 4. Can `@MockBean` be used if the bean is not already defined in the application context?

## Yes

`@MockBean` can create and add a mock bean even if the original bean does not exist.

Example:

```java
@SpringBootTest
class MyTest {

    @MockBean
    private PaymentService paymentService;

}
```

Spring will:

- Create a Mockito mock
- Add it to the application context
- Inject it where needed

---

## If the bean already exists

`@MockBean` replaces the existing bean with a mock.

This is useful for:

- Unit testing
- Integration testing
- Avoiding external API calls
- Avoiding database access

---

# Summary

| Question | Answer |
|---|---|
| Configure context with `@SpringBootTest` | Use properties, classes, or profiles |
| Exclude auto configuration | Use `@EnableAutoConfiguration(exclude=...)` |
| Default context cache size | 32 |
| No caching effect | Slower tests and more resource usage |
| Can `@MockBean` create missing bean? | Yes |
