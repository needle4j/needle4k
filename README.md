[![Build Status](https://secure.travis-ci.org/needle4k/needle4j.png)](https://travis-ci.org/needle4j/needle4k)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.needle4k/needle4k/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.needle4k/needle4k)

# needle4k - Effective Unit Testing

[@NeedleProject](https://twitter.com/NeedleProject)

<table style="vertical-align: top; border: none;">
  <tr>
    <td style="vertical-align: top; border: none;"> 
        <img alt="Needle Coffee Cups" src="https://www.needle4j.org/images/coffee.jpg">
    </td>
    <td style="vertical-align: top;border: none; width: 200px">
        <b>needle4k is a lightweight framework for testing (Java EE/Jakarta EE) components in isolation. Using needle4k it is easy to
        configure your tests in order to automatically inject mock and real objects into tested components.</b>
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
* Pluggable Mock providers [EasyMock](https://www.easymock.org/) and [Mockito](https://mockito.org/), on particular

# Getting started

Add the following dependencies to your pom.xml file to get started using Needle:

```
<dependency>
    <groupId>org.needle4j</groupId>
    <artifactId>needle4j</artifactId>
    <version>2.3</version>
    <scope>test</scope>
</dependency>

(plus junit, mockito, ...)
``` 

Implementing your first Needle Test:

```
public class UserDaoTest {

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule();

    @Rule
    public NeedleRule needleRule = new NeedleRule(databaseRule);

    @ObjectUnderTest
    private UserDao userDao;

    @Test
    public void testFindByUsername() throws Exception {
        final User user = new UserTestdataBuilder(
        databaseRule.getEntityManager()).buildAndSave();

        User userFromDb =
            userDao.findBy(user.getUsername(), user.getPassword());

        Assert.assertEquals(user.getId(), userFromDb.getId());
    }
}
``` 

For the documentation and more examples please refer to the maven site.

## Licensing

Needle is licensed under GNU Lesser General Public License (LGPL) version 2.1 or later.

## Needle URLs

* Needle Home Page: http://www.needle4j.org
* Source Code:      https://github.com/needle4j/needle4j
* Issue Tracking:   https://github.com/needle4j/needle4j/issues
* [needle4j@ohloh.net](https://www.ohloh.net/p/needle4j)
* [Gitter chat](https://gitter.im/needle4j)

## Release Nodes

Version 3.0 - Upgrade to JDK 11 and Hibernate 5.6, Removal of deprecated code, cleanup code

Version 2.2 - https://github.com/akquinet/needle/issues?milestone=1&state=closed

Previous Versions - https://github.com/akquinet/needle/blob/master/src/docs/dist/changelog.txt

