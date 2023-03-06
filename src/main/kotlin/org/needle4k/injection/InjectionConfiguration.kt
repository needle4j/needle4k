package org.needle4k.injection

import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.mock.MockAnnotationProcessor
import org.needle4k.mock.MockProvider
import org.needle4k.mock.SpyProvider
import org.needle4k.processor.ChainedNeedleProcessor
import org.needle4k.processor.PostConstructProcessor
import org.slf4j.LoggerFactory
import java.util.*

@Suppress("unused")
class InjectionConfiguration(val needleConfiguration: NeedleConfiguration) {
  // Default InjectionProvider for annotations
  private val injectionProviderList = ArrayList<InjectionProvider<*>>()

  // Global InjectionProvider for custom implementation
  private val globalInjectionProviderList = ArrayList<InjectionProvider<*>>()

  // Test-specific custom injection provider
  private val testInjectionProvider = ArrayList<InjectionProvider<*>>()

  // all with priority order
  val allInjectionProvider get() = listOf(testInjectionProvider, globalInjectionProviderList, injectionProviderList).flatten()

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
    addCdiInstance()

    needleConfiguration.injectionAnnotationRegistry.allAnnotations().forEach {
      add<Any>(it)
    }

    initGlobalInjectionAnnotation()
    initGlobalInjectionProvider()

    injectionProviderList.add(0, MockProviderInjectionProvider(mockProvider))
  }

  private fun addCdiInstance() {
    val instanceClass = needleConfiguration.reflectionHelper.forName("javax.enterprise.inject.Instance")
      ?: needleConfiguration.reflectionHelper.forName("jakarta.enterprise.inject.Instance")

    if (instanceClass != null) {
      injectionProviderList.add(CDIInstanceInjectionProvider(instanceClass, this))
    }
  }

  private fun <T> add(clazz: Class<out Annotation>) {
    LOG.debug("Register injection handler for class {}", clazz)

    if (clazz.name.endsWith(".annotation.Resource")) {
      injectionProviderList.add(ResourceMockInjectionProvider(this))
    } else {
      injectionProviderList.add(DefaultMockInjectionProvider<T>(clazz, this))
    }

    addInjectionAnnotation(clazz)
  }

  private fun initGlobalInjectionAnnotation() {
    val customInjectionAnnotations = needleConfiguration.customInjectionAnnotations
    addGlobalInjectionAnnotation(customInjectionAnnotations)
  }

  private fun addGlobalInjectionAnnotation(customInjectionAnnotations: Collection<Class<out Annotation>>) {
    for (annotation in customInjectionAnnotations) {
      addInjectionAnnotation(annotation)
      globalInjectionProviderList.add(0, DefaultMockInjectionProvider<Any>(annotation, this))
    }
  }

  private fun initGlobalInjectionProvider() {
    val customInjectionProviders: Set<Class<InjectionProvider<*>>> = needleConfiguration.customInjectionProviderClasses
    val reflectionUtil = needleConfiguration.reflectionHelper

    for (injectionProviderClass in customInjectionProviders) {
      try {
        val injection: InjectionProvider<*> = reflectionUtil.createInstance(injectionProviderClass)

        globalInjectionProviderList.add(0, injection)
      } catch (e: Exception) {
        LOG.warn("Could not create an instance of injection provider $injectionProviderClass", e)
      }
    }

    for (supplierClass in needleConfiguration.customInjectionProviderInstancesSupplierClasses) {
      try {
        val supplier: InjectionProviderInstancesSupplier = reflectionUtil.createInstance(supplierClass)

        globalInjectionProviderList.addAll(0, supplier.get())
      } catch (e: Exception) {
        LOG.warn("Could not create an instance of injection provider instance supplier $supplierClass", e)
      }
    }
  }

  fun addInjectionProvider(vararg injectionProvider: InjectionProvider<*>) {
    for (provider in injectionProvider) {
      testInjectionProvider.add(0, provider)
    }
  }

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

  private fun createMockProvider(): MockProvider {
    val className = needleConfiguration.mockProviderClassName
    val helper = needleConfiguration.reflectionHelper
    val mockProviderClass = helper.lookupClass(MockProvider::class.java, className)
      ?: throw IllegalStateException("Could not load mock provider class: '$className'")

    return helper.createInstance(mockProviderClass)
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(InjectionConfiguration::class.java)
  }
}