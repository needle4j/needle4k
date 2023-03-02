package org.needle4k.injection

import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.mock.MockAnnotationProcessor
import org.needle4k.mock.MockProvider
import org.needle4k.mock.SpyProvider
import org.needle4k.processor.ChainedNeedleProcessor
import org.slf4j.LoggerFactory
import java.util.*

@Suppress("unused")
class InjectionConfiguration(val needleConfiguration: NeedleConfiguration) {
  // Default InjectionProvider for annotations
  private val injectionProviderList: List<InjectionProvider<*>> = ArrayList()

  // Global InjectionProvider for custom implementation
  private val globalInjectionProviderList: List<InjectionProvider<*>> = ArrayList()

  // Test-specific custom injection provider
  private val testInjectionProvider = ArrayList<InjectionProvider<*>>()

  // all with priority order
  val allInjectionProvider get() = listOf(testInjectionProvider, globalInjectionProviderList, injectionProviderList).flatten()

  private val supportedAnnotations get() = needleConfiguration.injectionAnnotationRegistry.allAnnotations()

  val mockProvider = createMockProvider()
  val spyProvider: SpyProvider get() = if (mockProvider is SpyProvider) mockProvider else SpyProvider.FAKE

  // TODO
//  private val postConstructProcessor: PostConstructProcessor  = PostConstructProcessor(
//      POSTCONSTRUCT_CLASSES, needleConfiguration.getPostConstructExecuteStrategy()
//    )
//  private val injectionIntoAnnotationProcessor: InjectionAnnotationProcessor= InjectionAnnotationProcessor(
//      IsSupportedAnnotationPredicate(
//        this
//      )
//    )
//  private val testcaseInjectionProcessor: TestcaseInjectionProcessor = TestcaseInjectionProcessor(this)
  private val mockAnnotationProcessor = MockAnnotationProcessor(this)
  private val chainedNeedleProcessor = ChainedNeedleProcessor(
    mockAnnotationProcessor
// TODO
    //     , injectionIntoAnnotationProcessor, testcaseInjectionProcessor
  )
/*
  init {
    addCdiInstance()
    add(INJECT_CLASS)
    add(EJB_CLASS)
    add(PERSISTENCE_CONTEXT_CLASS)
    add(PERSISTENCE_UNIT_CLASS)
    addResource()
    initGlobalInjectionAnnotation()
    initGlobalInjectionProvider()
    injectionProviderList.add(0, MockProviderInjectionProvider(mockProvider))
    injectionProvider = Arrays.asList(testInjectionProvider, globalInjectionProviderList, injectionProviderList)
  }

  private fun addResource() {
    if (RESOURCE_CLASS != null) {
      addInjectionAnnotation(RESOURCE_CLASS)
      injectionProviderList.add(ResourceMockInjectionProvider(this))
    }
  }

  private fun addCdiInstance() {
    if (CDI_INSTANCE_CLASS != null) {
      // addInjectionAnnotation(RESOURCE_CLASS);
      injectionProviderList.add(CDIInstanceInjectionProvider(CDI_INSTANCE_CLASS, this))
    }
  }

  private fun add(clazz: Class<*>?) {
    if (clazz != null) {
      LOG.debug("register injection handler for class {}", clazz)
      injectionProviderList.add(DefaultMockInjectionProvider(clazz, this))
      addInjectionAnnotation(clazz)
    }
  }

  fun addInjectionProvider(vararg injectionProvider: InjectionProvider<*>) {
    for (provider in injectionProvider) {
      testInjectionProvider.add(0, provider)
    }
  }

  private fun initGlobalInjectionAnnotation() {
    val customInjectionAnnotations = needleConfiguration.customInjectionAnnotations
    addGlobalInjectionAnnotation(customInjectionAnnotations)
  }

  fun addGlobalInjectionAnnotation(customInjectionAnnotations: Collection<Class<Annotation>>) {
    for (annotation in customInjectionAnnotations) {
      addInjectionAnnotation(annotation)
      globalInjectionProviderList.add(0, DefaultMockInjectionProvider(annotation, this))
    }
  }

  private fun initGlobalInjectionProvider() {
    val customInjectionProviders: Set<Class<InjectionProvider<*>>> = needleConfiguration
      .getCustomInjectionProviderClasses()
    for (injectionProviderClass in customInjectionProviders) {
      try {
        val injection: InjectionProvider<*> = ReflectionUtil.createInstance(injectionProviderClass)
        globalInjectionProviderList.add(0, injection)
      } catch (e: Exception) {
        LOG.warn("could not create an instance of injection provider $injectionProviderClass", e)
      }
    }
    for (supplierClass in needleConfiguration
      .getCustomInjectionProviderInstancesSupplierClasses()) {
      try {
        val supplier: InjectionProviderInstancesSupplier = ReflectionUtil.createInstance(supplierClass)
        globalInjectionProviderList.addAll(0, supplier.get())
      } catch (e: Exception) {
        LOG.warn("could not create an instance of injection provider instance supplier $supplierClass", e)
      }
    }
  }

 */

  @Suppress("UNCHECKED_CAST")
  private fun addInjectionAnnotation(clazz: Class<*>) {
    if (clazz.isAnnotation) { // TODO
      needleConfiguration.injectionAnnotationRegistry.addAnnotation(clazz as Class<out Annotation>)
    }
  }

  fun isAnnotationSupported(annotation: Class<out Annotation>)=
     needleConfiguration.injectionAnnotationRegistry.isRegistered(annotation)

  fun handleInjectionProvider(
    injectionProviders: Collection<InjectionProvider<*>>,
    injectionTargetInformation: InjectionTargetInformation<*>
  ): Pair<Any, Any>? {
    for (provider in injectionProviders) {
      if (provider.verify(injectionTargetInformation)) {
        val `object`: Any = provider.getInjectedObject(injectionTargetInformation.injectedObjectType)
        val key = provider.getKey(injectionTargetInformation)

        return key to `object`
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
    // TODO
//    private val POSTCONSTRUCT_CLASSES: Set<Class<*>> = ReflectionUtil
//      .getClasses("javax.annotation.PostConstruct")
//    private val RESOURCE_CLASS: Class<*>? = forName("javax.annotation.Resource")
//    private val INJECT_CLASS: Class<*> = forName("javax.inject.Inject")
//    private val CDI_INSTANCE_CLASS: Class<*>? = forName("javax.enterprise.inject.Instance")
//    private val EJB_CLASS: Class<*> = forName("javax.ejb.EJB")
//    private val PERSISTENCE_CONTEXT_CLASS: Class<*> = forName("javax.persistence.PersistenceContext")
//    private val PERSISTENCE_UNIT_CLASS: Class<*> = forName("javax.persistence.PersistenceUnit")

  }
}