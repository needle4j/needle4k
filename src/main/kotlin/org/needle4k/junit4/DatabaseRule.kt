package org.needle4k.junit4

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.DatabaseInjector
import org.needle4k.db.DatabaseInjectorConfiguration
import org.needle4k.db.operation.DBOperation
import org.needle4k.injection.InjectionProvider
import javax.persistence.EntityManager

/**
 * The [DatabaseRule] provides access to the configured Database and
 * execute optional configured [DBOperation] before and after a test.
 *
 * <pre>
 * public class EntityTestcase {
 * &#064;Rule
 * public DatabaseRule databaseRule = new DatabaseRule();
 *
 * &#064;Test
 * public void testPersist() throws Exception {
 * User user = new User();
 * // ...
 * databaseRule.getEntityMnager().persist(user);
 * }
 * }
</pre> *
 *
 * @see DatabaseInjector
 */

class DatabaseRule
@JvmOverloads constructor(
  needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration.INSTANCE,
  private val databaseInjector: DatabaseInjector = DatabaseInjector(DatabaseInjectorConfiguration(needleConfiguration))
) : MethodRule, InjectionProvider<Any> by databaseInjector {
  val configuration: DatabaseInjectorConfiguration get() = databaseInjector.configuration
  val entityManager: EntityManager get() = configuration.entityManager
  val needleConfiguration get() = databaseInjector.configuration.needleConfiguration

  override fun apply(base: Statement, method: FrameworkMethod, target: Any): Statement {
    return object : Statement() {
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