package org.needle4k.junit4.testrule

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.DatabaseInjector
import org.needle4k.db.DatabaseInjectorConfiguration
import javax.persistence.EntityManager

class DatabaseTestRule @JvmOverloads constructor(
  needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration.INSTANCE,
) : TestRule {
  private val databaseInjector = DatabaseInjector(DatabaseInjectorConfiguration(needleConfiguration))

  val entityManager: EntityManager get() = databaseInjector.configuration.entityManager

  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      @Throws(Throwable::class)
      override fun evaluate() {
        try {
          databaseInjector.before()
          base.evaluate()
        } finally {
          databaseInjector.after()
        }
      }
    }
  }
}