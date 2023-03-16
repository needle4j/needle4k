[![Build Status](https://secure.travis-ci.org/needle4k/needle4j.png)](https://travis-ci.org/needle4j/needle4k)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.needle4k/needle4k/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.needle4k/needle4k)

# needle4k - Effective Unit Testing

[@NeedleProject](https://twitter.com/NeedleProject)

<table style="vertical-align: top; border: none;">
  <tr>
    <td style="vertical-align: top; border: none;"> 
        <img alt="Needle Coffee Cups" src="https://www.needle4j.org/images/coffee.jpg">
    </td>
    <td style="vertical-align: top;border: none">
        <b>needle4k is a lightweight framework for testing (Java EE/Jakarta EE) components in isolation. Using needle4k it is easy to
        configure your tests in order to automatically inject mock and real objects into tested components.</b>
        <br/>
        needle4k is a [Kotlin-based](https://kotlinlang.org/) relaunch und upgrade of the reliable needle4j framework.  
    </td>
  </tr>
</table>

# Core Features:

* Automated setup of components annotated with @ObjectUnderTest
* Constructor, Method and Field based dependency injection
* Injection of Mock objects by default
* Extensible by providing custom injection providers

* Database testing using Hibernate
* Optionally clear database after each test
* EntityManager creation and injection
* As well Java EE as Jakarta EE are supported
* Transaction and reflection utilities
* All major test frameworks supported out of the box: JUnit4, JUnit5, TestNG
* Pluggable Mock providers [EasyMock](https://easymock.org/) and [Mockito](https://mockito.org/), in particular

# Getting started

Add the following dependencies to your pom.xml file to get started using Needle:

```
<dependency>
    <groupId>org.needle4k</groupId>
    <artifactId>needle4k</artifactId>
    <version>4.0.0</version>
    <scope>test</scope>
</dependency>
``` 

(plus JUnit, Mockito, AssertJ,...)

## Implementing your first JUnit5 Needle Test

```
@ExtendWith(JPANeedleExtension.class)
public class UserDaoTest {
    @InjectIntoMany // Mock object will be created and injected automatically
    private MetricsService metricsService;

    @Inject // Inject components directely into test
    private EntityManager entityManager;

    @ObjectUnderTest // Create component and inject dependencies into it
    private UserDao userDao;

    @Test
    public void testFindByUsername() throws Exception {
        entityManager.persist(new User("demo"));
        
        User userFromDb = userDao.findBy("demo");
        assertThat(userFromDb).isEqualTo(user);
    }
}
``` 
# Documentation

For documentation and more examples please refer to the [needle4k website](https://www.needle4j.org/).

## Licensing

needle4k is licensed under GNU Lesser General Public License (LGPL) version 2.1 or later.

## Needle URLs

* Needle Home Page: https://www.needle4j.org
* Source Code:      https://github.com/needle4j/needle4k
* Issue Tracking:   https://github.com/needle4j/needle4k/issues
* [needle4j@ohloh.net](https://www.ohloh.net/p/needle4j)
* [Gitter chat](https://gitter.im/needle4j)
