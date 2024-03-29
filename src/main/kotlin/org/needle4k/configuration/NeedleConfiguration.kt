package org.needle4k.configuration

import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionProviderInstancesSupplier
import org.needle4k.registries.AnnotationRegistry

interface NeedleConfiguration {
  val configurationProperties: MutableMap<String, String>

  val injectionAnnotationRegistry: AnnotationRegistry
  val postConstructAnnotationRegistry: AnnotationRegistry

  val customInjectionAnnotations: Set<Class<out Annotation>>
  val customInjectionProviderClasses: Set<Class<InjectionProvider<*>>>
  val customInjectionProviderInstancesSupplierClasses: Set<Class<InjectionProviderInstancesSupplier>>

  val persistenceUnitName get() = configurationProperties[PERSISTENCE_UNIT_NAME_KEY]!!
  val mockProviderClassName get() = configurationProperties[MOCK_PROVIDER_KEY]!!
  val dbOperationClassName get() = configurationProperties[DB_OPERATION_KEY]!!
  val postConstructExecuteStrategy get() = PostConstructExecuteStrategy.valueOf(configurationProperties[POST_CONSTRUCT_EXECUTE_STRATEGY]!!)
}

val WELL_KNOWN_INJECTION_ANNOTATION_CLASSES = listOf(
  "javax.ejb.EJB",
  "jakarta.ejb.EJB",
  "javax.annotation.Resource",
  "jakarta.annotation.Resource",
  "javax.inject.Inject",
  "jakarta.inject.Inject",
  "javax.persistence.PersistenceContext",
  "jakarta.persistence.PersistenceContext",
  "javax.persistence.PersistenceUnit",
  "jakarta.persistence.PersistenceUnit",
  "org.picocontainer.annotations.Inject",
  "org.springframework.beans.factory.annotation.Autowired"
)

// Applies to methods and constructors
val WELL_KNOWN_POSTCONSTRUCTION_ANNOTATION_CLASSES = listOf(
  "javax.annotation.PostConstruct", "jakarta.annotation.PostConstruct"
)