package org.needle4k

import org.needle4k.db.JPAInjector
import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.LazyInjectionProvider
import org.needle4k.reflection.ReflectionUtil

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractNeedleRule(val needleInjector: NeedleInjector, vararg injectionProviders: InjectionProvider<*>) {
  val needleConfiguration get() = needleInjector.configuration.needleConfiguration
  val needleContext get() = needleInjector.context
  val jpaInjector = JPAInjector(JPAInjectorConfiguration(needleConfiguration))
  val jpaInjectorConfiguration get() = jpaInjector.configuration
  val entityManager get() = jpaInjectorConfiguration.entityManager
  val entityManagerFactory get() = jpaInjectorConfiguration.entityManagerFactory

  init {
    needleInjector.addInjectionProvider(*injectionProviders)
    needleInjector.addInjectionProvider(LazyInjectionProvider(NeedleInjector::class.java) { needleInjector })
    needleInjector.addInjectionProvider(LazyInjectionProvider(ReflectionUtil::class.java) { needleConfiguration.reflectionUtil })
  }

  fun addJPAInjectionProvider() {
    needleInjector.addInjectionProvider(jpaInjector)
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPAInjector::class.java) { jpaInjector })
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPAInjectorConfiguration::class.java) { jpaInjectorConfiguration })
  }

  protected fun runBeforeTest(testInstance: Any) {
    needleInjector.initTestInstance(testInstance)
    needleInjector.before()
    jpaInjector.before()
  }

  protected fun runAfterTest() {
    needleInjector.after()
    jpaInjector.after()
  }

  fun <X> getInjectedObject(key: Any): X? = needleInjector.getInjectedObject<X>(key)

  fun <X> getInjectedObject(key: Class<X>): X? = needleInjector.getInjectedObject<X>(key)
}