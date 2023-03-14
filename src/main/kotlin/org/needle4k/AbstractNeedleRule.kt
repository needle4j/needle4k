package org.needle4k

import org.needle4k.db.JPAInjector
import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.LazyInjectionProvider
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractNeedleRule(val needleInjector: NeedleInjector, vararg injectionProviders: InjectionProvider<*>)  {
  val needleConfiguration get() = needleInjector.configuration.needleConfiguration
  val jpaInjector: JPAInjector? get() = needleInjector.configuration.getInjectionProvider(JPAInjector::class.java)
  val jpaInjectorConfiguration: JPAInjectorConfiguration
    get() = jpaInjector?.configuration
      ?: throw IllegalStateException("NeedleRule was not configured with JPA injection provider")
  val entityManager: EntityManager get() = jpaInjectorConfiguration.entityManager
  val entityManagerFactory: EntityManagerFactory get() = jpaInjectorConfiguration.entityManagerFactory

  init {
    needleInjector.addInjectionProvider(*injectionProviders)
    needleInjector.addInjectionProvider(LazyInjectionProvider(NeedleInjector::class.java) { needleInjector })
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPAInjector::class.java) {
      jpaInjector
        ?: throw IllegalStateException("NeedleRule was not configured with JPA injection provider")
    })
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPAInjectorConfiguration::class.java) { jpaInjectorConfiguration })
  }

  open fun withJPAInjection(): AbstractNeedleRule {
    if (!needleInjector.configuration.hasInjectionProvider(JPAInjector::class.java)) {
      needleInjector.addInjectionProvider(JPAInjector(JPAInjectorConfiguration(needleConfiguration)))
    }

    return this
  }

  protected fun runBeforeTest(testInstance: Any) {
    needleInjector.initTestInstance(testInstance)
    needleInjector.before()
    jpaInjector?.before()
  }

  protected fun runAfterTest() {
    needleInjector.after()
    jpaInjector?.after()
  }

  fun <X> getInjectedObject(key: Any): X? = needleInjector.getInjectedObject<X>(key)

  fun <X> getInjectedObject(key: Class<X>): X? = needleInjector.getInjectedObject<X>(key)
}