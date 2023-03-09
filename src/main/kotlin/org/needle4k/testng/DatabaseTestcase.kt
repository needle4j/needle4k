package org.needle4k.testng

import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.DatabaseInjector
import org.needle4k.db.DatabaseInjectorConfiguration
import org.needle4k.injection.InjectionProvider
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

open class DatabaseTestcase @JvmOverloads constructor(
  needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration.INSTANCE,
  private val databaseInjector: DatabaseInjector = DatabaseInjector(DatabaseInjectorConfiguration(needleConfiguration))
) : InjectionProvider<Any> by databaseInjector {
  val entityManager: EntityManager get() = databaseInjector.configuration.entityManager
  val entityManagerFactory: EntityManagerFactory get() = databaseInjector.configuration.entityManagerFactory

  /**
   * {@inheritDoc}
   */
  @AfterMethod
  @Throws(Exception::class)
  fun after() {
    databaseInjector.after()
  }

  /**
   * {@inheritDoc}
   */
  @BeforeMethod
  @Throws(Exception::class)
  fun before() {
    databaseInjector.before()
  }
}