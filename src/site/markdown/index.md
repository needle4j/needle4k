# Overview

![needle4k](../resources/images/coffee.jpg)

**needle4k is a lightweight framework for testing
([Java EE/Jakarta EE](https://jakarta.ee/), [Spring Bean](https://spring.io/), e.g.) components in isolation.
Using needle4k it is easy to configure your tests in order to automatically inject mock and real objects into tested components.
needle4k is a [Kotlin-based](https://kotlinlang.org/) rewrite and upgraded version of the reliable
[needle4j](https://needl4j.org/) framework.**

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

<!-- TOC -->
* [Configuration:](configuration.md)
* [Database testing:](database-testing.md)
    * [Links](#links)
<!-- TOC -->