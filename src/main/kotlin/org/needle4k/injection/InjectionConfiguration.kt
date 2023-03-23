package org.needle4k.injection

import org.needle4k.NeedleSession
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.mock.MockAnnotationProcessor
import org.needle4k.mock.MockProvider
import org.needle4k.mock.SpyProvider
import org.needle4k.processor.ChainedNeedleProcessor
import org.needle4k.processor.PostConstructProcessor
import org.needle4k.reflection.ReflectionUtil
import org.slf4j.LoggerFactory
import java.util.*

@Suppress("unused")
class InjectionConfiguration(val needleConfiguration: NeedleConfiguration) {
  // Default InjectionProvider for annotations
  private val defaultInjectionProviders = ArrayList<InjectionProvider<*>>()

  // InjectionProvider from custom implementations
  private val customInjectionProviders = ArrayList<InjectionProvider<*>>()

  // Test-specific custom injection provider
  private val testInjectionProviders = ArrayList<InjectionProvider<*>>()

  // all with priority order
  val allInjectionProviders get() = listOf(testInjectionProviders, customInjectionProviders, defaultInjectionProviders).flatten()

  val supportedAnnotations get() = needleConfiguration.injectionAnnotationRegistry.allAnnotations()

  val mockProvider: MockProvider by lazy { createMockProvider() }
  val spyProvider: SpyProvider get() = if (mockProvider is SpyProvider) mockProvider as SpyProvider else SpyProvider.FAKE

  val postConstructProcessor = PostConstructProcessor(this)

  private val injectionIntoAnnotationProcessor = InjectionAnnotationProcessor()
  private val testcaseInjectionProcessor = TestcaseInjectionProcessor(this)
  private val mockAnnotationProcessor = MockAnnotationProcessor(this)

  val chainedNeedleProcessor =
    ChainedNeedleProcessor(mockAnnotationProcessor, injectionIntoAnnotationProcessor, testcaseInjectionProcessor)

  init {
    needleConfiguration.injectionAnnotationRegistry.allAnnotations().forEach {
      addDefaultInjectionAnnotationClass<Any>(it)
    }
    addCdiInstance()

    initCustomInjectionAnnotations()
    initCustomInjectionProviders()

    addDefaultInjectionProvider(MockProviderInjectionProvider { mockProvider })
  }

  private fun addCdiInstance() {
    val instanceClass = ReflectionUtil.forName("javax.enterprise.inject.Instance")
      ?: ReflectionUtil.forName("jakarta.enterprise.inject.Instance")

    if (instanceClass != null) {
      addDefaultInjectionProvider(CDIInstanceInjectionProvider(instanceClass, this))
    }
  }

  private fun <T : Any> addDefaultInjectionAnnotationClass(clazz: Class<out Annotation>) {
    LOG.debug("Register injection handler for class {}", clazz)

    // Special handling
    if (clazz.name.endsWith(".annotation.Resource")) {
      addDefaultInjectionProvider(ResourceMockInjectionProvider(clazz, this))
    } else {
      addDefaultInjectionProvider(DefaultMockInjectionProvider<T>(clazz, this))
    }

    addInjectionAnnotation(clazz)
  }

  private fun initCustomInjectionAnnotations() {
    val customInjectionAnnotations = needleConfiguration.customInjectionAnnotations

    for (annotation in customInjectionAnnotations) {
      addCustomInjectionAnnotationClass(annotation)
    }
  }

  fun addCustomInjectionAnnotationClass(annotation: Class<out Annotation>) {
    addInjectionAnnotation(annotation)

    customInjectionProviders.add(0, DefaultMockInjectionProvider<Any>(annotation, this))
  }

  private fun initCustomInjectionProviders() {
    val customInjectionProviders: Set<Class<InjectionProvider<*>>> = needleConfiguration.customInjectionProviderClasses

    for (injectionProviderClass in customInjectionProviders) {
      try {
        val injection: InjectionProvider<*> = ReflectionUtil.createInstance(injectionProviderClass)

        this.customInjectionProviders.add(0, injection)
      } catch (e: Exception) {
        LOG.warn("Could not create an instance of injection provider $injectionProviderClass", e)
      }
    }

    for (supplierClass in needleConfiguration.customInjectionProviderInstancesSupplierClasses) {
      try {
        val supplier: InjectionProviderInstancesSupplier = ReflectionUtil.createInstance(supplierClass)

        this.customInjectionProviders.addAll(0, supplier.get())
      } catch (e: Exception) {
        LOG.warn("Could not create an instance of injection provider instance supplier $supplierClass", e)
      }
    }
  }

  fun addInjectionProvider(vararg injectionProviders: InjectionProvider<*>) {
    testInjectionProviders.addAll(0, injectionProviders.asList())
  }

  internal fun addDefaultInjectionProvider(vararg injectionProviders: InjectionProvider<*>) {
    defaultInjectionProviders.addAll(0, injectionProviders.asList())
  }

  fun hasInjectionProvider(injectorClass: Class<out InjectionProvider<*>>) =
    getInjectionProvider(injectorClass) != null

  @Suppress("UNCHECKED_CAST")
  fun <X : InjectionProvider<*>> getInjectionProvider(injectorClass: Class<X>): X? =
    testInjectionProviders.map { it }.firstOrNull { it.javaClass == injectorClass } as X?

  @Suppress("UNCHECKED_CAST")
  private fun addInjectionAnnotation(clazz: Class<out Annotation>) {
    needleConfiguration.injectionAnnotationRegistry.addAnnotation(clazz)
  }

  fun isAnnotationSupported(annotation: Class<out Annotation>) =
    needleConfiguration.injectionAnnotationRegistry.isRegistered(annotation)

  fun handleInjectionProvider(
    injectionProviders: Collection<InjectionProvider<*>>,
    injectionTargetInformation: InjectionTargetInformation<*>
  ): Pair<Any, Any?>? {
    for (provider in injectionProviders) {
      if (provider.verify(injectionTargetInformation)) {
        val injectedObject = provider.getInjectedObject(injectionTargetInformation.injectedObjectType)
        val key = provider.getKey(injectionTargetInformation)

        return key to injectedObject
      }
    }

    return null
  }

  internal fun createMockProvider(): MockProvider {
    val className = needleConfiguration.mockProviderClassName
    val mockProviderClass = ReflectionUtil.lookupClass(MockProvider::class.java, className)
      ?: throw IllegalStateException("Could not load mock provider class: '$className'")

    return ReflectionUtil.createInstance(mockProviderClass)
  }

  fun initInjectionProviders(needleSession: NeedleSession) {
    allInjectionProviders.forEach { it.initialize(needleSession) }
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(InjectionConfiguration::class.java)
  }
}