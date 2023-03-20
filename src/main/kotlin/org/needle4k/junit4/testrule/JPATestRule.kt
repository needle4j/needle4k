package org.needle4k.junit4.testrule

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.JPAInjector
import org.needle4k.db.JPAInjectorConfiguration
import javax.persistence.EntityManager

class JPATestRule @JvmOverloads constructor(
  needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration(),
) : TestRule {
  private val jpaInjector = JPAInjector(JPAInjectorConfiguration(needleConfiguration))

  val entityManager: EntityManager get() = jpaInjector.configuration.entityManager

  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      override fun evaluate() {
        try {
          jpaInjector.before()
          base.evaluate()
        } finally {
          jpaInjector.after()
        }
      }
    }
  }
}