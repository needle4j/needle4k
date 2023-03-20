# Migration guide

**needle4k** is an offspring of the well-known [needle4k](https://needle4j.org/) framework.

If you want to migrate your projects to **needle4k**, please regard these notes:

* There is no extra `DatabaseRule` for JUnit4 tests anymore. Instead, you simply augment the `NeedleRule` like this: 
```java 
@Rule
public final NeedleRule needleRule = new NeedleRule().withJPAInjection();
```
* needle4k supports JUnit5 tests using an "extension":
```java
@ExtendWith(NeedleExtension.class)
public class PlainNeedleTest { ... }
```
```java
@ExtendWith(JPANeedleExtension.class)
public class EntityManagerTest { ... }
```
* The default mock provider is now Mockito instead of EasyMock, since Mockito requires much less boilerplate code and is easier to use. You can however change this behavior by defining a `needle.properties` file containing:
```properties 
mock.provider=org.needle4k.mock.EasyMockProvider
```
* Within `needle.properties`, there is no need anymore to define JDBC configuration properties, like `jdbc.user`. Instead, these properties are taken from the `META-INF/persistence.xml` file.
* needle4k supports Java EE annotations as well as Jakarta EE ones, i.e. those where the package names start with `jakarta.*` instead of `javax.*`. You just need to change the Maven dependency to:
```xml
<dependency>
    <groupId>org.needle4k</groupId>
    <artifactId>needle4k-jakarta</artifactId>
    <version>4.0.1</version>
    <scope>test</scope>
</dependency>
``` 
