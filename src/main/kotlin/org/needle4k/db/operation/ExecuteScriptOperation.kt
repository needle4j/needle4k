package org.needle4k.db.operation

import org.needle4k.db.JPAInjectorConfiguration
import java.sql.SQLException

/**
 * Execute before and after sql scripts in test setup and tear down.
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
class ExecuteScriptOperation(configuration: JPAInjectorConfiguration) : AbstractDBOperation(configuration) {
  /**
   * Execute <pre>before.sql</pre> script in test setup.
   *
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  override fun setUpOperation() {
    execute(BEFORE_SCRIPT_NAME)
  }

  /**
   * Execute <pre>after.sql</pre> script in test tear down.
   *
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  override fun tearDownOperation() {
    execute(AFTER_SCRIPT_NAME)
  }

  @Throws(SQLException::class)
  private fun execute(filename: String) {
    configuration.execute {
      it.createStatement().use { statement ->
        executeScript(filename, statement)
      }
    }
  }

  companion object {
    private const val BEFORE_SCRIPT_NAME = "before.sql"
    private const val AFTER_SCRIPT_NAME = "after.sql"
  }
}