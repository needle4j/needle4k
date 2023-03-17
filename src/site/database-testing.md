# Database Testing

When unit-testing your services, it is usually recommended to mock calls to DAOs oder database dependent services.
However, you will also need to test against a real database, e.g. to make sure that your queries work as expected.

Again, take a look at the [quickstart project](https://github.com/needle4j/needle4k-quickstart) for ready-to-use examples.

## Database Testcases

**needle4k** can inject anything you want... This includes creating and injecting `EntityManager` instances into your objects
under test automagically. You simply need to provide a JPA-conforming `META-INF/persistence.xml` for your JPA tests
and add the JDBC driver the test dependencies.

The following listing below shows a complete example of a `persistence.xml` file.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd"
             version="1.0">
    <persistence-unit name="TestDataModel" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <class>org.needle4k.quickstart.user.Person</class>
        <class>org.needle4k.quickstart.user.Address</class>

        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.hsqldb.jdbc.JDBCDriver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:hsqldb:mem:memoryDB"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>

            <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.HSQLDialect"/>
            <!--
                   <property name="hibernate.show_sql" value="true"/>
                  <property name="hibernate.debug" value="true"/>
                  <property name="hibernate.format_sql" value="true"/>
                  <property name="hibernate.use_sql_comments" value="true"/>
                -->
        </properties>
    </persistence-unit>
</persistence>
```

The UserTest below checks the JPA mapping against a real database.

```java
public class UserTest
{
  @Rule
  public NeedleRule needleRule = new NeedleRule().withJPAInjection();

  @Inject
  private EntityManager entityManager;

  @Test
  public void testPersist() throws Exception
  {
    final EntityManager entityManager = databaseRule.getEntityManager();

    // You can access the entity manager either way
    assertSame(this.entityManager, entityManager);

    final User user = new UserTestdataBuilder(entityManager).buildAndSave();
    final User userFromDb = entityManager.find(User.class, user.getId());

    assertEquals(user.getId(), userFromDb.getId());
  }
}
```

## Transaction helper

The EntityManager is the primary interface used by application developers to interact with the underlying database. Many
operations
must be executed in a transaction which is not started in the test component, because it is usually maintained by the application
server.
To run your code using DB transactions you might want to use the `TransactionHelper` utility.

```java
public class UserTest
{
  @Rule
  public NeedleRule needleRule = new NeedleRule().withJPAInjection();

  @Inject
  private TransactionHelper transactionHelper;

  @Test
  public void testPersist() throws Exception
  {
    final User user = new User();

    transactionHelper.execute(entityManager -> {
      entityManager.persist(user);
      entityManager.flush();
    });
  }
}
```

The above example illustrates the use of the `TransactionHelper` class and the execution of a transaction. It also provides
several utility methods for saving and loading objects.

## Database operation

A common issue in unit tests that access a real database is their effect on the state of the persistence store. They usually
expect a certain
state at the beginning and alter it at runtime which of course will affect other tests.
To address that problem optional Database operations can be executed before and after test execution, in order to clean up or set
up data.

The following operation are provided by default:

 Class                                                  | Description                                                                                                               
--------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------
 org.needle4k.db.operation.ExecuteScriptOperation       | Execute sql scripts during test setup and tear down. A `/before.sql` and `/after.sql` script has to be provided on the classpath. 
 org.needle4k.db.operation.h2.H2DeleteOperation         | Deletes all rows of all tables of the H2 database.                                                                        
 org.needle4k.db.operation.hsql.HSQLDeleteOperation`    | Deletes all rows of all tables of the HSQL database.                                                                      
 org.needle4k.db.operation.derby.DerbyLDeleteOperation` | Deletes all rows of all tables of the Derby database.                                                                     

To use own Database operation implementations, extend the abstract base class `org.needle4k.db.operation.AbstractDBOperation`
and configured the "db.operation" property in the `needle.properties` file.

## Injectable database resources

In combination with the NeedleRule, the following resources are injectable by the DatabaseTestcase.

1. EntityManager
2. EntityManagerFactory
3. EntityTransaction
4. TransactionHelper
5. JPAInjectorConfiguration

