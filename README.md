[![Build Status](https://secure.travis-ci.org/needle4k/needle4j.png)](https://travis-ci.org/needle4j/needle4k)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.needle4k/needle4k/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.needle4k/needle4k)

![Banner](src/site/images/banner.png)

# Isolated unit testing for Java EE and Spring components

**needle4k is a lightweight framework for testing 
([Java EE/Jakarta EE](https://jakarta.ee/), [Spring Bean](https://spring.io/), e.g.) components in isolation.
Using needle4k it is easy to configure your tests in order to automatically inject mock and real objects into tested components.
needle4k is a [Kotlin-based](https://kotlinlang.org/) rewrite and upgraded version of the reliable
[needle4j](https://needle4j.org/) framework.**

There is a [quickstart project](https://github.com/needle4j/needle4k-quickstart) demonstrating all features.

# Core Features:

* Automated setup of components annotated with @ObjectUnderTest
* Constructor, Method and Field based dependency injection
* Injection of Mock objects by default
* Extensible by providing custom injection providers
* Database testing using [Hibernate](http://www.hibernate.org)
* Optionally clear database after each test
* EntityManager creation and injection
* As well Java EE as Jakarta EE are supported
* Transaction and reflection utilities
* needle4k can be used with [JUnit4](https://junit.org/junit4/), [JUnit5](https://junit.org/junit5/) or [TestNG](http://testng.org/).
* Pluggable Mock providers: [EasyMock](https://easymock.org/) and [Mockito](https://mockito.org/), in particular

# Documentation

For documentation and more examples please refer to the [needle4k site docs](src/site/index.md).

# Getting started

Add the following dependencies to your pom.xml file to get started using needle4k:

```xml
<dependency>
    <groupId>org.needle4k</groupId>
    <artifactId>needle4k</artifactId>
    <version>4.0.1</version>
    <scope>test</scope>
</dependency>
```

Use this dependency if you are using Jakarta EE &geq; 9:

```xml
<dependency>
    <groupId>org.needle4k</groupId>
    <artifactId>needle4k-jakarta</artifactId>
    <version>4.0.1</version>
    <scope>test</scope>
</dependency>
``` 

(plus JUnit, Mockito, AssertJ, and other testing frameworks...)

## Gradle dependency

```gradle
testCompile("org.needle4k:needle4k:4.0.1")
```

## Implementing your first JUnit5 test in Java

```java
@ExtendWith(JPANeedleExtension.class)
public class UserDaoTest {
  @InjectIntoMany // Mock object will be created and injected automatically everywhere
  private MetricsService metricsService;

  @javax.inject.Inject // Inject components directly into test using standard annotations
  private EntityManager entityManager;

  @ObjectUnderTest // Create testet component and inject dependencies into it
  private UserDao userDao;

  @Test
  public void testFindByUsername() throws Exception {
    entityManager.persist(new User("demo"));
        
    User userFromDb = userDao.findByName("demo");
    assertThat(userFromDb).isEqualTo(user);
  }
}
```
## Implementing your first JUnit5 test in Kotlin

```kotlin
@ExtendWith(JPANeedleExtension::class)
class UserDaoTest {
  @InjectIntoMany // Mock object will be created and injected automatically everywhere
  private lateinit var metricsService : MetricsService

  @javax.inject.Inject // Inject components directly into test using standard annotations
  private lateinit var entityManager: EntityManager 

  @ObjectUnderTest // Create testet component and inject dependencies into it
  private lateinit var userDao: UserDao

  @Test
  fun `Find user by name`() {
    entityManager.persist(User("demo"))
        
    val userFromDb = userDao.findByName("demo")
    assertThat(userFromDb).isEqualTo(user)
  }
}
```

## Licensing

needle4k is licensed under the GNU Lesser General Public License (LGPL) version 2.1 or later.

## Developers

needle4k is based on the [needle4j](https://github.com/needle4j/needle4j) framework originally written by
[Heinz Wilming](mailto:heinz.wilming@akquinet.de),
[Jan Galinski](mailto:jan.galinski@holisticon.de) and [Alphonse Bendt](https://github.com/abendt).

The rewrite has been developed by [Markus Dahm](mailto:markus.dahm@akquinet.de).

## Links

* Source Code:      https://github.com/needle4j/needle4k
* Issue Tracking:   https://github.com/needle4j/needle4k/issues
