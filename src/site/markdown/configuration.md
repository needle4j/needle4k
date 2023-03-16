# Configuration

This chapter describes how to set up and configure your **needle4k** tests.

## Requirements

Ensure that you have a [JDK11+](https://www.oracle.com/java/technologies/downloads/#java11) installed.

## Maven dependency configuration

If you are using [Maven](http://maven.apache.org/) as your build tool add the following single dependency to your 
pom.xml file to get started with needle4k:

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

To reduce complexity needle4k only two transitive dependencies: It uses [SLF4J](https://www.slf4j.org/) for its logging
and the [Kotlin standard library](https://kotlinlang.org/api/latest/jvm/stdlib/).

Thus, you are not restricted to use a specific versions of JUnit, Mockito or Hibernate. On
the other hand, you will have to explicitly configure dependencies for
Hibernate as the JPA provider and Mockito as the mock provider. Typically, this look like

```xml
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate.version}</version>
        </dependency>

        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.version}</version>
        </dependency>
```

## Needle configuration properties

The needle4k default configuration may be modified in a `needle.properties` file in the classpath root. 
I.e., Needle will look for a file `/needle.properties` somewhere in the classpath.

You may also configure the framework programmatically, please refer to the [quickstart project](https://github.com/needle4j/needle4k-quickstart) to see examples for this.

### Configuration of additional custom injection annotations and injection provider.

Property Name  | Description
------------- | -------------
custom.injection.annotations  | Comma separated list of the fully qualified name of the annotation classes. A standard mock provider will be created for each annotation.
custom.injection.provider.classes | Comma separated list of the fully qualified name of the injection provider implementations.
custom.instances.supplier.classes | Comma separated list of the fully qualified name of the instances supplier implementations.

### Configuration of mock provider.

Property Name  | Description
------------- | -------------
mock.provider | The fully qualified name of an implementation of the MockProvider interface. There is an implementation of EasyMock
org.needle4k.mock.EasyMockProvider and Mockito org.needle4k.mock.MockitoProvider. **EasyMock is the default configuration.**

### Configuration of JPA, Database operation and JDBC connection.

Property Name  | Description
------------- | -------------
mock.provider | The fully qualified name of an implementation of the MockProvider interface. There is an implementation of EasyMock org.needle4k.mock.EasyMockProvider and Mockito org.needle4k.mock.MockitoProvider. **EasyMock is the default configuration.**
persistenceUnit.name | The persistence unit name. Default is TestDataModel
hibernate.cfg.filename | XML configuration file to configure Hibernate (eg. /hibernate.cfg.xml)
db.operation | Optional database operation on test setup and tear down. Value is the fully qualified name of an implementation of the
AbstractDBOperation base class. There is an implementation for script execution org.needle4k.db.operation.ExecuteScriptOperation
and for the HSQL DB to delete all tables org.needle4k.db.operation.hsql.HSQLDeleteOperation.
jdbc.url | The JDBC driver specific connection url.
jdbc.driver | The fully qualified class name of the driver class.
jdbc.user | The JDBC user name used for the database connection.
jdbc.password | The JDBC password used for the database connection.

The JDBC configuration properties are only required if database operation and JPA 1.0 are used. Otherwise, the JDBC properties are
related to the standard property names of JPA 2.0.

### Example configuration

A typical `needle.properties` file might look like this:

```
db.operation=de.akquinet.jbosscc.needle.db.operation.hsql.HSQLDeleteOperation
jdbc.driver=org.hsqldb.jdbcDriver
jdbc.url=jdbc:hsqldb:mem:memoryDB
jdbc.user=sa
jdbc.password=
```

## Logging

needle4k uses the Simple Logging Facade for Java (SLF4J).
[SLF4J](http://www.slf4j.org/manual.html) serves as a simple facade or
abstraction for various logging frameworks. The SLF4J distribution ships
with several JAR files referred to as “SLF4J bindings”, with each
binding corresponding to a supported framework.

For logging within the test, the following optional dependency may be
added to the classpath:

```xml
<dependency>
   <groupId>org.slf4j</groupId>
   <artifactId>slf4j-simple</artifactId>
   <scope>test</scope>
</dependency>
```
