package org.needle4k.junit4

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.configuration.db.operation.DBOperation
import org.needle4k.db.DatabaseInjector
import org.needle4k.db.DatabaseInjectorConfiguration

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
) : MethodRule {
  private val databaseInjector = DatabaseInjector(DatabaseInjectorConfiguration(needleConfiguration))

  override fun apply(base: Statement, method: FrameworkMethod, target: Any): Statement {
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