# Testing with Mock objects

Mock objects are a useful way to write unit tests for objects that have collaborators. **needle4k** generates mock objects
dynamically for dependencies of the components under test by default. Out-of-the-box
**needle4k** has implementations for EasyMock and Mockito.

To use other mock frameworks, the interface `org.needle4k.mock.MockProvider` must be implemented and configured
using the "mock.provider" property in the `needle.properties` file.

## Create a Mock Object

To create a Mock object, you can annotate a field with the annotation `@Mock`.

```java
public class Test
{
  @Rule
  public NeedleRule needleRule = new NeedleRule();

  @Mock
  private EntityManager entityManagerMock;

  @Test
  public void test() throws Exception
  {
      ...
  }
}
```

The dependencies of an object under test (annotated with `@ObjectUnderTest`) are automatically initialized. These dependencies can
also
be injected into the testcase by using the usual injection annotations like `javax.inject.Inject`.

## EasyMock

The EasyMockProvider creates “nice” Mock objects by default. Such nice mocks allow all method calls and returns appropriate empty
values e.g. 0, null or false. If necessary, all mocks can also be converted to use another policy by calling resetAllToNice(),
resetAllToDefault() or resetAllToStrict().

The EasyMockProvider implementation is a subclass of EasyMockSupport. EasyMockSupport is a class that meant to be used as a helper or base
class to your test cases. It will automatically register all created mocks and to replay, reset or verify them in batch instead of
explicitly.

The following test illustrates the usage of EasyMock with Needle and the injection of generated mock objects.

```java
public class AuthenticatorTest
{
  @Rule
  public final NeedleRule needleRule = new NeedleRule();

  @ObjectUnderTest
  private Authenticator authenticator;

  @Inject
  private EasyMockProvider mockProvider;

  @Inject
  private UserDao userDaoMock;

  @Test
  public void testAuthenticate() throws Exception
  {
    final User user = new UserTestdataBuilder().build();
    final String username = "username";
    final String password = "password";

    EasyMock.expect(userDaoMock.findBy(username, password)).andReturn(user);

    mockProvider.replayAll();
    boolean authenticated = authenticator.authenticate(username, password);
    Assert.assertTrue(authenticated);
    mockProvider.verifyAll();
  }
}
```

To use EasyMock, the EasyMockProvider must be configured in the `needle.properties` file and the mockito library must be
present on test classpath.

    mock.provider=org.needle4k.mock.EasyMockProvider

For more details about EasyMock, please refer to the [EasyMock](https://easymock.org) documentation.

## Mockito

Needle has also an mock provider implementation for Mockito. Mockito generates Mock objects, where by default the return value of a method is
null, an empty collection or the appropriate primitive value.

Mockito is the default mock provider in **needle4k**.

The following test illustrates the usage of Mockito with Needle.

```java
public class AuthenticatorTest
{
  @Rule
  public NeedleRule needleRule = new NeedleRule();

  @ObjectUnderTest
  private Authenticator authenticator;

  @Inject
  private UserDao userDaoMock;

  @Test
  public void testAuthenticate() throws Exception
  {
    final User user = new UserTestdataBuilder().build();
    final String username = "username";
    final String password = "password";

    Mockito.when(userDaoMock.findBy(username, password)).thenReturn(user);

    Assert.assertTrue(authenticator.authenticate(username, password));

  }
}
```

For more details about Mockito, please refer to the [Mockito](http://mockito.org) documentation.
