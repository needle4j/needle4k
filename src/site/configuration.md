# Configuration

This chapter describes how to set up and configure your **needle4k** tests.

Please refer to the [quickstart project](https://github.com/needle4j/needle4k-quickstart) to see more examples.

## Requirements

Ensure that you have a [JDK11+](https://www.oracle.com/java/technologies/downloads/#java11) installed.

## Maven dependency configuration

If you are using [Maven](http://maven.apache.org/) as your build tool, add the following single dependency to your
pom.xml file to get started with **needle4k**:

```xml
<dependency>
    <groupId>org.needle4k</groupId>
    <artifactId>needle4k</artifactId>
    <scope>test</scope>
    <version>4.0.1</version>
</dependency>
```

Check for the most current version at the [maven central repo](http://mvnrepository.com/artifact/org.needle4k/needle4k).

### Transitive dependencies

To reduce complexity the needle4k framework has only two hard *runtime* dependencies:

* [SLF4J-API](https://www.slf4j.org/) for internal logging
* and the [Kotlin standard library](https://kotlinlang.org/api/latest/jvm/stdlib/).

and some *compile-time* dependencies you may safely ignore, if you're not using them:

* Hibernate
* Java Persistence API
* JUnit 4 & 5
* TestNG

You are not restricted to use a specific versions of EJB, JPA, JUnit, Mockito or Hibernate. But you will have to explicitly configure
dependencies for Hibernate as the JPA provider and Mockito as the mock provider. However, this should have been configured
in your project anyway.

Typically, dependencies look like

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>${hibernate.version}</version>
    <scope>test</scope>
</dependency>
``` 
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>${mockito.version}</version>
    <scope>test</scope>
</dependency>
```
```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>${hsqldb.version}</version>
    <scope>test</scope>
</dependency>
```

## needle4k configuration properties

The needle4k default configuration may be modified in a `needle.properties` file in the classpath root.
I.e., **needle4k** will look for a file `/needle.properties` in the classpath.

### Configuration of additional custom injection annotations and injection provider

 Property Name                     | Description                                                                                                                               
-----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------
 custom.injection.annotations      | Comma separated list of the fully qualified name of the annotation classes. A standard mock provider will be created for each annotation. 
 custom.injection.provider.classes | Comma separated list of the fully qualified name of the injection provider implementations.                                               
 custom.instances.supplier.classes | Comma separated list of the fully qualified name of the instances supplier implementations.                                               

### Configuration of mock provider

 Property Name | Description                                                                                                         
---------------|---------------------------------------------------------------------------------------------------------------------
 mock.provider | The fully qualified name of an implementation of the MockProvider interface. There is an implementation of EasyMock 

org.needle4k.mock.EasyMockProvider and Mockito org.needle4k.mock.MockitoProvider. **The default provider is Mockito.**

### Configuration of JPA, Database operation and JDBC connection

 Property Name        | Description                                                                                                                                                                                                                                                                                                                                                            
----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 persistenceUnit.name | The persistence unit name, as defined in `META-INF/persistence.xml`. **Default is "TestDataModel"**                                                                                                                                                                                                                                                                    
 db.operation         | Optional database operation on test setup and tear down. Value is the fully qualified name of an implementation of the AbstractDBOperation base class. There is an implementation for script execution org.needle4k.db.operation.ExecuteScriptOperation **It defaults to the HSQL DB command to delete all tables org.needle4k.db.operation.hsql.HSQLDeleteOperation** 

### Example configuration

In general, you do not need to override configuration properties. However, a *custom* `needle.properties` file could look like
this:

```properties
# When using H2 instead of HSQLDB
db.operation=de.akquinet.jbosscc.needle.db.operation.h2.H2DeleteOperation
custom.injection.annotations=org.needle4k.quickstart.annotations.CustomInjectionAnnotation
# When using EasyMock instead of Mockito
mock.provider=org.needle4k.mock.EasyMockProvider
#
# These are the default values and not need to be overridden usually
#
#db.operation=org.needle4k.db.operation.hsql.HSQLDeleteOperation
#persistenceUnit.name=TestDataModel
#postconstruct.executestrategy=DEFAULT
```

### Programmatic configuration

You may also configure the framework programmatically, e.g. in order to use EasyMock instead of Mockito

```java
import static org.needle4k.configuration.ConfigurationLoaderKt.MOCK_PROVIDER_KEY;

public class EasyMockProviderTest
{
  @Rule
  public final NeedleRule needleRule = new NeedleRule()
  {
    {
      final Map<String, String> configurationProperties = getNeedleConfiguration().getConfigurationProperties();

      configurationProperties.put(MOCK_PROVIDER_KEY, EasyMockProvider.class.getName());
    }
  };
  ...
}
```

## Logging

**needle4k** uses the Simple Logging Facade for Java (SLF4J).

[SLF4J](http://www.slf4j.org/manual.html) serves as a simple facade or abstraction for various logging frameworks. The SLF4J
distribution ships with several JAR files referred to as “SLF4J bindings”, with each binding corresponding to a supported
framework.

For logging within the tests, the following optional dependency should be added to the classpath:

```xml

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.6</version>
    <scope>test</scope>
</dependency>
```
