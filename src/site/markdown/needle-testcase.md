# Needle Testcase

## ObjectUnderTest instantiation and initialization

Needle4j may automatically instantiate all objects under test for you. The
Needle test case scans all fields of the test class for annotations and
creates a completely initialized instance. Alternatively, the developer
may of course instantiate the field herself, too.

Multiple fields can be annotated with the `@ObjectUnderTest` annotation.
The annotation can optionally be configured with the implementation of
the type and an id. The id may be used for additional injections
(explained later). When an object under test is already instantiated,
only the dependency injection will be done.

### PostConstruct lifecycle callback

During the object under test instantiation it is possible to execute
lifecycle methods annotated with PostConstruct. The PostConstruct
annotation is used on a method that needs to be executed after
dependency injection is done to perform any initialization. By default
lifecycle methods are ignored. The execution can be activated by the
annotation @ObjectUnderTest.

```java
@Stateless
public class UserDao {

  @PostConstruct
  public void init(){
     ...
  }
}
```

```java
public class UserDaoTest {
  @Rule
  public final NeedleRule needleRule = new NeedleRule();

  @ObjectUnderTest(postConstruct = true)
  private UserDao userDao;

  @Test
  public void test(){
       ...
  }    
}
```

Note: The behavior in an inheritance hierarchy is not defined by the common annotations specification.

### Injection

Needle supports field, constructor and method injection by evaluating
`@EJB`, `@Resource`, `@PersistenceContext`, `@PersistenceUnit` and
`@Inject` annotations. Note that the annotation class needs to be
available one the classpath of the test execution. By default, Mock
objects for the dependencies are created and injected.

The injected (mock or real) objects can be also injected into your
testcase. Test case injection can be done in the same way as the
injection of dependencies in the production code. I.e., you may write
`@Inject` within your test case to access the generated components.

All standard Java EE annotations and any additionally configured
injection annotation can be used to inject a reference into the
testcase.

```java
public class AuthenticatorTest {
  @Rule
  public NeedleRule needleRule = new NeedleRule();

  @ObjectUnderTest
  private Authenticator authenticator = new Authenticator();

  @Inject
  private EasyMockProvider mockProvider;

  @Inject
  private UserDao userDaoMock;

  @Test
  public void testAuthenticate() {
     ...
  }
}
```

It is also possible to use the API to get a reference of an injected
object (this does not create the instance, it has to be created before).

    UserDao injectedUserDao = needleRule.getInjectedObject(UserDao.class);

The key is generated from the respective injection provider. By default,
the class object of the associated injection point is used as the key or
– in the case of resource injection – the mapped name of the resource.

## Custom injection provider

Needle is fully extensible, you may implement your own injection providers or register additional annotations.

The following example shows the registration of additional annotations
in the `needle.properties`.

    custom.injection.annotations=org.jboss.seam.annotations.In, org.jboss.seam.annotations.Logger

It is also possible to implement custom providers. A custom injection
provider must implement the `org.needle4j.injection.InjectionProvider` interface.

```java
public class CurrentUserInjectionProvider implements InjectionProvider<User> {
  private final User currentUser = new User();

  @Override
  public User getInjectedObject(Class<?> injectionPointType) {
     return currentUser;
  }

  @Override
  public boolean verify(InjectionTargetInformation information) {
    return information.isAnnotationPresent(CurrentUser.class);
  }

  @Override
  public Object getKey(InjectionTargetInformation information) {
    return CurrentUser.class;
  }
}
```

A custom injection provider can be provided for a specific test or as a
global provider (again in the `needle.properties` file).

    @Rule
    public NeedleRule needleRule = new NeedleRule(new CurrentUserInjectionProvider());


    custom.injection.provider.classes=fqn.CurrentUserInjectionProvider

If you need to configure multiple InjectionProviders at once, it is
possible to use the InjectionProviderInstancesSupplier. It returns a Set
of InjectionProviders. A custom injection provider supplier must
implement the `org.needle4j.injection.InjectionProviderInstancesSupplier` interface.

```java
public class FooBarInjectionProviderInstancesSupplier implements InjectionProviderInstancesSupplier {
  private final Foo foo = new Foo();
  private final Bar bar = new Bar();
       
  private final Set<InjectProvider<?>> providers = new HashSet<InjectionProvider<?>>();
       
  public FooBarInjectionProviderInstancesSupplier() {
    providers.add(InjectionProviders.providerForInstance(foo));
    providers.add(InjectionProviders.providerForInstance(bar));
  }

  @Override
  public Set<InjectProvider<?>> get() {
    return providers;
  }
}
```

A custom injection provider instances supplier can be provided for a
specific test or also as a global provider (again in the `needle.properties` file).

    @Rule
    public NeedleRule needleRule = NeedleRuleBuilder.needleRule().addSupplier(new FooBarInjectionProviderInstancesSupplier());

    custom.instances.supplier.classes=fqn.FooBarInjectionProviderInstancesSupplier

### Wiring of object dependencies

Sometimes you may want to provide your own objects as components or
influence the wiring of complex object graphs. Typically, you may want
to mix mock objects and “real” objects, depending on the focus of your
test.

The object referenced by the field annotated with `@InjectIntoMany` is
injected into all fields annotated with `@ObjectUnderTest`.

In the following example UserDao is not a mock, but a fully initialized
component. It is injected into the Authenticator object (or any other
component annotated with `@ObjectUnderTest`).

```java
@ObjectUnderTest
private Authenticator authenticator;

@InjectIntoMany
@ObjectUnderTest
private UserDao userDao;
```

If you need more fine-grained configuration you may also use the `@InjectInto` annotation to specify the component IDs 
where the object shall be injected. The ID corresponds to the field name of the target object by default.

```java
@ObjectUnderTest
private Authenticator authenticator;

@InjectInto(targetComponentId="authenticator")
@ObjectUnderTest
private UserDao userDao;
```
