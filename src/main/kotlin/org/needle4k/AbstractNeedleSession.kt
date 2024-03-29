package org.needle4k

import org.needle4k.db.JPAInjectionProvider
import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.LazyInjectionProvider
import org.needle4k.reflection.ReflectionUtil

@Suppress("MemberVisibilityCanBePrivate", "LeakingThis")
abstract class AbstractNeedleSession(
  override val needleInjector: NeedleInjector,
  vararg injectionProviders: InjectionProvider<*>
) : NeedleSession {
  override val needleConfiguration get() = needleInjector.configuration.needleConfiguration
  override val needleContext get() = needleInjector.context
  override val jpaInjectionProvider by lazy { JPAInjectionProvider(JPAInjectorConfiguration(needleConfiguration)) }
  override val jpaInjectorConfiguration get() = jpaInjectionProvider.configuration
  val entityManager get() = jpaInjectorConfiguration.entityManager
  val entityManagerFactory get() = jpaInjectorConfiguration.entityManagerFactory

  private var before: () -> Unit = { needleInjector.before() }
  private var after: () -> Unit = { needleInjector.after() }

  init {
    configure()

    needleInjector.addInjectionProvider(*injectionProviders)
    needleInjector.addDefaultInjectionProvider(LazyInjectionProvider(NeedleInjector::class.java) { needleInjector },
      LazyInjectionProvider(ReflectionUtil::class.java) { ReflectionUtil })
  }

  protected open fun configure() {
  }

  fun addJPAInjectionProvider() {
    needleInjector.addDefaultInjectionProvider(jpaInjectionProvider,
      LazyInjectionProvider(JPAInjectionProvider::class.java) { jpaInjectionProvider },
      LazyInjectionProvider(JPAInjectorConfiguration::class.java) { jpaInjectorConfiguration })

    before = {
      needleInjector.before()
      jpaInjectionProvider.before()
    }

    after = {
      needleInjector.after()
      jpaInjectionProvider.after()
    }
  }

  protected fun runBeforeTest(testInstance: Any) {
    needleInjector.initTestInstance(testInstance, this)

    before()
  }

  protected fun runAfterTest() {
    after()
  }

  fun <X> getInjectedObject(key: Any): X? = needleInjector.getInjectedObject<X>(key)

  fun <X> getInjectedObject(key: Class<X>): X? = needleInjector.getInjectedObject<X>(key)
}