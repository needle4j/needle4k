package org.needle4k.configuration

import org.needle4k.configuration.db.operation.JdbcConfiguration
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionProviderInstancesSupplier
import org.needle4k.reflection.ReflectionUtil
import org.needle4k.registries.AnnotationRegistry

class NeedleConfiguration(needleProperties: String = CUSTOM_CONFIGURATION_FILENAME) {
  val reflectionHelper = ReflectionUtil(this)
  val configurationProperties = ConfigurationLoader(needleProperties).configProperties

  val injectionAnnotationRegistry = AnnotationRegistry(this)
  val postconstructAnnotationRegistry = AnnotationRegistry(this)

  val customInjectionAnnotations: Set<Class<out Annotation>>
    get() = ClassListParser(this).lookup(CUSTOM_INJECTION_ANNOTATIONS_KEY)
  val customInjectionProviderClasses: Set<Class<InjectionProvider<*>>>
    get() = ClassListParser(this).lookup(CUSTOM_INJECTION_PROVIDER_CLASSES_KEY)
  val customInjectionProviderInstancesSupplierClasses: Set<Class<InjectionProviderInstancesSupplier>>
    get() = ClassListParser(this).lookup(CUSTOM_INSTANCES_SUPPLIER_CLASSES_KEY)

  val persistenceUnitName get() = configurationProperties[PERSISTENCE_UNIT_NAME_KEY]!!
  val mockProviderClassName get() = configurationProperties[MOCK_PROVIDER_KEY]!!
  val dbOperationClassName get() = configurationProperties[DB_OPERATION_KEY]!!
  val postConstructExecuteStrategy get() = PostConstructExecuteStrategy.valueOf(configurationProperties[POST_CONSTRUCT_EXECUTE_STRATEGY]!!)
  val jdbcConfiguration
    get() = JdbcConfiguration(
      configurationProperties[JDBC_URL_KEY]!!, configurationProperties[JDBC_USER_KEY]!!,
      configurationProperties[JDBC_PASSWORD_KEY]!!, configurationProperties[JDBC_DRIVER_KEY]!!
    )

  init {
    injectionAnnotationRegistry.addAnnotation("javax.ejb.EJB").addAnnotation("jakarta.ejb.EJB")
      .addAnnotation("javax.annotation.Resource").addAnnotation("jakarta.annotation.Resource")
      .addAnnotation("javax.inject.Inject").addAnnotation("jakarta.inject.Inject")
      .addAnnotation("javax.persistence.PersistenceContext").addAnnotation("jakarta.persistence.PersistenceContext")
      .addAnnotation("javax.persistence.PersistenceUnit").addAnnotation("jakarta.persistence.PersistenceUnit")
      .addAnnotation("org.picocontainer.annotations.Inject")
      .addAnnotation("org.springframework.beans.factory.annotation.Autowired")

    postconstructAnnotationRegistry.addAnnotation("javax.annotation.PostConstruct")
      .addAnnotation("jakarta.annotation.PostConstruct").addAnnotation("javax.inject.Inject")
      .addAnnotation("jakarta.inject.Inject")
  }
}
