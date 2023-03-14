package org.needle4k.testng

import org.needle4k.NeedleInjector
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.LazyInjectionProvider
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import javax.persistence.EntityManager

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractNeedleTestcase(
  val needleInjector: NeedleInjector,
  vararg injectionProviders: InjectionProvider<*>
) {
  private val databaseTestcase: DatabaseTestcase =
    injectionProviders.filterIsInstance<DatabaseTestcase>().map { it }.firstOrNull()
      ?: DatabaseTestcase(needleInjector.configuration.needleConfiguration)
  val entityManager: EntityManager get() = databaseTestcase.entityManager

  init {
    needleInjector.addInjectionProvider(*injectionProviders)
    needleInjector.addInjectionProvider(LazyInjectionProvider(NeedleInjector::class.java) { needleInjector })
  }

  @BeforeMethod
  fun beforeNeedleTestcase() {
    needleInjector.initTestInstance(this)
    needleInjector.before()
    databaseTestcase.before()
  }

  @AfterMethod
  fun afterNeedleTestcase() {
    needleInjector.after()
    databaseTestcase.after()
  }
}