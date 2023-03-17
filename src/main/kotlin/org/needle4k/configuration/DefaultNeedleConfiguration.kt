package org.needle4k.configuration

import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionProviderInstancesSupplier
import org.needle4k.registries.AnnotationRegistry

class DefaultNeedleConfiguration(needlePropertiesFile: String = CUSTOM_CONFIGURATION_FILENAME) : NeedleConfiguration {
  override var configurationProperties = ConfigurationLoader(needlePropertiesFile).configProperties

  override val injectionAnnotationRegistry = AnnotationRegistry()
  override val postConstructAnnotationRegistry = AnnotationRegistry()

  override val customInjectionAnnotations: Set<Class<out Annotation>>
    get() = ClassListParser(this).lookup(CUSTOM_INJECTION_ANNOTATIONS_KEY)
  override val customInjectionProviderClasses: Set<Class<InjectionProvider<*>>>
    get() = ClassListParser(this).lookup(CUSTOM_INJECTION_PROVIDER_CLASSES_KEY)
  override val customInjectionProviderInstancesSupplierClasses: Set<Class<InjectionProviderInstancesSupplier>>
    get() = ClassListParser(this).lookup(CUSTOM_INSTANCES_SUPPLIER_CLASSES_KEY)

  init {
    WELL_KNOWN_INJECTION_ANNOTATION_CLASSES.forEach { injectionAnnotationRegistry.addAnnotation(it) }
    WELL_KNOWN_POSTCONSTRUCTION_ANNOTATION_CLASSES.forEach { postConstructAnnotationRegistry.addAnnotation(it) }
  }
}
