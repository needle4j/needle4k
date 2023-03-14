package org.needle4k.testng

import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.JPAInjector
import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.injection.InjectionProvider
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

open class DatabaseTestcase @JvmOverloads constructor(
  needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration.INSTANCE,
  val jpaInjector: JPAInjector = JPAInjector(JPAInjectorConfiguration(needleConfiguration))
) : InjectionProvider<Any> by jpaInjector {
  val entityManager: EntityManager get() = jpaInjector.configuration.entityManager
  val entityManagerFactory: EntityManagerFactory get() = jpaInjector.configuration.entityManagerFactory
  val needleConfiguration get() = jpaInjector.configuration.needleConfiguration

  /**
   * {@inheritDoc}
   */
  @BeforeMethod
  fun before() {
    jpaInjector.before()
  }

  /**
   * {@inheritDoc}
   */
  @AfterMethod
  fun after() {
    jpaInjector.after()
  }
}