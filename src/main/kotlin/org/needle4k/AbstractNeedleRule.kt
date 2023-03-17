package org.needle4k

import org.needle4k.db.JPAInjector
import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.LazyInjectionProvider
import org.needle4k.reflection.ReflectionHelper

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractNeedleRule(val needleInjector: NeedleInjector, vararg injectionProviders: InjectionProvider<*>) {
  val needleConfiguration get() = needleInjector.configuration.needleConfiguration
  val needleContext get() = needleInjector.context
  val jpaInjector by lazy { JPAInjector(JPAInjectorConfiguration(needleConfiguration)) }
  val jpaInjectorConfiguration get() = jpaInjector.configuration
  val entityManager get() = jpaInjectorConfiguration.entityManager
  val entityManagerFactory get() = jpaInjectorConfiguration.entityManagerFactory

  private var before: () -> Unit = { needleInjector.before() }
  private var after: () -> Unit = { needleInjector.after() }

  init {
    needleInjector.addInjectionProvider(*injectionProviders)
    needleInjector.addInjectionProvider(LazyInjectionProvider(NeedleInjector::class.java) { needleInjector })
    needleInjector.addInjectionProvider(LazyInjectionProvider(ReflectionHelper::class.java) { ReflectionHelper })
  }

  fun addJPAInjectionProvider() {
    needleInjector.addInjectionProvider(jpaInjector)
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPAInjector::class.java) { jpaInjector })
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPAInjectorConfiguration::class.java) { jpaInjectorConfiguration })

    before = {
      needleInjector.before()
      jpaInjector.before()
    }

    after = {
      needleInjector.after()
      jpaInjector.after()
    }
  }

  protected fun runBeforeTest(testInstance: Any) {
    needleInjector.initTestInstance(testInstance)

    before()
  }

  protected fun runAfterTest() {
    after()
  }

  fun <X> getInjectedObject(key: Any): X? = needleInjector.getInjectedObject<X>(key)

  fun <X> getInjectedObject(key: Class<X>): X? = needleInjector.getInjectedObject<X>(key)
}