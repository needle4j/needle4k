package org.needle4k

import org.needle4k.annotation.InjectInto
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.injection.*
import org.needle4k.mock.SpyProvider
import org.slf4j.LoggerFactory
import java.lang.reflect.*

/**
 * Abstract test case to process and initialize all fields annotated with
 * [ObjectUnderTest]. After initialization, [org.needle4k.annotation.InjectIntoMany] and
 * [InjectInto] annotations are processed for optional additional
 * injections.
 *
 *
 * Supported injections are:
 *
 *
 *  1. Constructor injection
 *  1. Field injection
 *  1. Method injection
 *
 *
 * @see ObjectUnderTest
 *
 * @see InjectInto
 *
 * @see org.needle4k.annotation.InjectIntoMany
 *
 * @see org.needle4k.injection.InjectionProvider
 *
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
class NeedleInjector constructor(
  val configuration: InjectionConfiguration,
  vararg injectionProviders: InjectionProvider<*>
) {
  private lateinit var context: NeedleContext

  init {
    addInjectionProvider(*injectionProviders)
  }

  fun addInjectionProvider(vararg injectionProvider: InjectionProvider<*>) {
    configuration.addInjectionProvider(*injectionProvider)
  }

  /**
   * Initialize all fields annotated with [ObjectUnderTest]. Is an
   * object under test annotated field already initialized, only the injection
   * of dependencies will be executed. After initialization,
   * [org.needle4k.annotation.InjectIntoMany] and [InjectInto] annotations are processed
   * for optional additional injections.
   *
   * @param test an instance of the test
   * @throws Exception thrown if an initialization error occurs.
   */
  @Throws(Exception::class)
  fun initTestInstance(test: Any) {
    LOG.info("Initializing testcase {}...", test)
    context = NeedleContext(test, configuration.needleConfiguration)

    processInjections(test)

    configuration.chainedNeedleProcessor.process(context)

    beforePostConstruct()
    configuration.postConstructProcessor.process(context)
  }

  private fun processInjections(test: Any) {
    val fields = context.getAnnotatedTestcaseFields(ObjectUnderTest::class.java)

    for (field in fields) {
      LOG.debug("found field {}", field.name)
      val objectUnderTestAnnotation = field.getAnnotation(ObjectUnderTest::class.java)

      try {
        val instance = setInstanceIfNotNull(field, objectUnderTestAnnotation, test)
        initInstance(instance)
      } catch (e: InstantiationException) {
        LOG.error(e.message, e)
      } catch (e: IllegalAccessException) {
        LOG.error(e.message, e)
      }
    }
  }

  /**
   * init mocks
   */
  fun beforePostConstruct() {}

  /**
   * Inject dependencies into the given instance. First, all field injections
   * are executed, if there exists an [InjectionProvider] for the
   * target. Then the method injection is executed, if the injection
   * annotations are supported.
   *
   * @param instance the instance to initialize.
   */
   fun initInstance(instance: Any) {
    injectIntoAnnotatedFields(instance)
    initMethodInjection(instance)
  }

  private fun initMethodInjection(instance: Any) {
    val needleConfiguration = configuration.needleConfiguration
    val reflectionUtil = needleConfiguration.reflectionHelper
    val registry = needleConfiguration.injectionAnnotationRegistry
    val methods = reflectionUtil.getMethods(instance.javaClass)
      .filter { registry.isRegistered(*it.declaredAnnotations) }

    for (method in methods) {
      val parameterInfos =
        createParameterInformation(method) { m: Method, p: Parameter, a: Annotation -> MethodTargetInformation(m, p, a) }
      val arguments = createArguments(parameterInfos)

      try {
        reflectionUtil.invokeMethod(method, instance, *arguments)
      } catch (e: Exception) {
        LOG.warn("Could not invoke method", e)
      }
    }
  }

  @Throws(ObjectUnderTestInstantiationException::class)
  fun getInstanceByConstructorInjection(implementation: Class<*>): Any? {
    val registry = configuration.needleConfiguration.injectionAnnotationRegistry
    val constructors = implementation.constructors.filter { registry.isRegistered(*it.declaredAnnotations) }

    for (constructor in constructors) {
      val parameterInfos =
        createParameterInformation(constructor) { c: Constructor<*>, p: Parameter, a: Annotation ->
          ConstructorTargetInformation(c, p, a)
        }
      val arguments = createArguments(parameterInfos)

      return try {
        constructor.newInstance(*arguments)
      } catch (e: Exception) {
        throw ObjectUnderTestInstantiationException(e)
      }
    }
    return null
  }

  private fun <T : Executable> createParameterInformation(
    method: T,
    creator: (method: T, parameter: Parameter, injectionAnnotation: Annotation) -> ExecutableTargetInformation<*>
  ): List<ExecutableTargetInformation<*>> {
    val registry = configuration.needleConfiguration.injectionAnnotationRegistry
    val methodAnnotation = registry.registeredAnnotation(*method.declaredAnnotations)!!

    return method.parameters.map {
      val injectionAnnotation: Annotation = registry.registeredAnnotation(*it.annotations) ?: methodAnnotation

      creator(method, it, injectionAnnotation)
    }
  }

  private fun createArguments(parameters: List<InjectionTargetInformation<*>>): Array<Any?> =
    parameters.map { inject(it)?.second }.toTypedArray()

  private fun injectIntoAnnotatedFields(instance: Any) {
    val needleConfiguration = configuration.needleConfiguration
    val registry = needleConfiguration.injectionAnnotationRegistry
    val reflectionUtil = needleConfiguration.reflectionHelper
    val fields: List<Field> = reflectionUtil.getAllFieldsWithSupportedAnnotation(instance.javaClass)

    for (field in fields) {
      val annotation = registry.registeredAnnotation(*field.declaredAnnotations)!!
      val injectionTargetInformation = FieldTargetInformation(field, annotation)
      val injection = inject(injectionTargetInformation)

      if (injection != null) {
        try {
          reflectionUtil.setField(field, instance, injection.second)
        } catch (e: Exception) {
          LOG.error(e.message, e)
        }
      }
    }
  }

  private fun setInstanceIfNotNull(field: Field, objectUnderTestAnnotation: ObjectUnderTest, test: Any): Any {
    val reflectionUtil = configuration.needleConfiguration.reflectionHelper
    val spyProvider: SpyProvider = configuration.spyProvider
    val id = objectUnderTestAnnotation.id.ifBlank { field.name }

    // First try
    var instance: Any? = reflectionUtil.getFieldValue(test, field)

    if (instance == null) {
      val implementation = if (objectUnderTestAnnotation.implementation.java !== Void::class.java)
        objectUnderTestAnnotation.implementation.java else field.type

      if (implementation.javaClass.isInterface) {
        throw ObjectUnderTestInstantiationException(
          "Could not create an instance of object under test $implementation, no implementation class found"
        )
      }

      // Second try
      instance = getInstanceByConstructorInjection(implementation)

      if (instance == null) {
        // Third try
        instance = createInstanceByNoArgConstructor(implementation)
      }
    }

    // create spy if required, else just return unmodified instance
    if (spyProvider.isSpyRequested(field) && spyProvider.isSpyPossible(field)) {
      instance = spyProvider.createSpyComponent(instance)
    }

    setField(field, test, instance)
    context.addObjectUnderTest(id, instance, objectUnderTestAnnotation)

    return instance
  }

  @Throws(ObjectUnderTestInstantiationException::class)
  fun setField(field: Field, test: Any, instance: Any?) {
    val reflectionUtil = configuration.needleConfiguration.reflectionHelper

    try {
      reflectionUtil.setField(field, test, instance)
    } catch (e: Exception) {
      throw ObjectUnderTestInstantiationException(e)
    }
  }

  @Throws(ObjectUnderTestInstantiationException::class)
  fun createInstanceByNoArgConstructor(implementation: Class<*>): Any {
    return try {
      implementation.getConstructor()
      implementation.getDeclaredConstructor().newInstance()
    } catch (e: NoSuchMethodException) {
      throw ObjectUnderTestInstantiationException(
        "Could not create an instance of object under test "
            + implementation + ",implementation has no public no-arguments constructor", e
      )
    } catch (e: Exception) {
      throw ObjectUnderTestInstantiationException(e)
    }
  }

  /**
   * Returns the injected object for the given key, or null if no object was
   * injected with the given key.
   *
   * @param key the key of the injected object, see
   * [InjectionProvider.getKey]
   * @return the injected object or null
   */
  fun <X> getInjectedObject(key: Any): X? = context.getInjectedObject<X>(key)

  private fun inject(injectionTargetInformation: InjectionTargetInformation<*>): Pair<Any, Any?>? {
    val injection = configuration.handleInjectionProvider(configuration.allInjectionProviders, injectionTargetInformation)

    return if (injection != null) {
      val injectionKey = injection.first
      val injectionValue = context.getInjectedObject<Any>(injectionKey) ?: injection.second

      context.addInjectedObject(injectionKey, injectionValue)

      injectionKey to injectionValue
    } else {
      null
    }
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(NeedleInjector::class.java)!!
  }
}
