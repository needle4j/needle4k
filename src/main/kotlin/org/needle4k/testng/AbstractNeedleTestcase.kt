package org.needle4k.testng

import org.needle4k.NeedleInjector
import org.needle4k.injection.InjectionProvider
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import javax.persistence.EntityManager

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractNeedleTestcase(
  val needleInjector: NeedleInjector,
  vararg injectionProviders: InjectionProvider<*>
) {
  private val databaseTestcase: DatabaseTestcase =
    injectionProviders.filter { it is DatabaseTestcase }.map { it as DatabaseTestcase }.firstOrNull()
      ?: throw IllegalArgumentException("No database testcase found in injector list")

  @BeforeMethod
  @Throws(Exception::class)
  fun beforeNeedleTestcase() {
    needleInjector.initTestInstance(this)

    databaseTestcase.before()
  }

  @AfterMethod
  @Throws(Exception::class)
  fun afterNeedleTestcase() {
    databaseTestcase.after()
  }

  protected val entityManager: EntityManager get() = databaseTestcase.entityManager
}