package org.needle4k.junit4

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.JPAInjector
import org.needle4k.db.DatabaseInjectorConfiguration
import org.needle4k.db.operation.DBOperation
import org.needle4k.injection.InjectionProvider
import javax.persistence.EntityManager

/**
 * The [DatabaseRule] provides access to the configured Database and
 * execute optional configured [DBOperation] before and after a test.
 *
 * <pre>
 * public class EntityTest {
 * &#064;Rule
 * public DatabaseRule databaseRule = new DatabaseRule();
 *
 * &#064;Test
 * public void testPersist() throws Exception {
 * User user = new User();
 * // ...
 * databaseRule.getEntityManager().persist(user);
 * }
 * }
</pre> *
 *
 * @see JPAInjector
 */

open class DatabaseRule
@JvmOverloads constructor(
  needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration.INSTANCE,
  private val jpaInjector: JPAInjector = JPAInjector(DatabaseInjectorConfiguration(needleConfiguration))
) : MethodRule, InjectionProvider<Any> by jpaInjector {
  val configuration: DatabaseInjectorConfiguration get() = jpaInjector.configuration
  val entityManager: EntityManager get() = configuration.entityManager
  val needleConfiguration get() = jpaInjector.configuration.needleConfiguration

  override fun apply(base: Statement, method: FrameworkMethod, target: Any): Statement {
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