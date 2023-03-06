package org.needle4k.configuration.db.operation

import org.needle4k.configuration.NeedleConfiguration
import org.slf4j.LoggerFactory
import java.sql.SQLException

/**
 * Execute before and after sql scripts in test setup and tear down.
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
class ExecuteScriptOperation(needleConfiguration: NeedleConfiguration) : AbstractDBOperation(needleConfiguration) {
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
    try {
      getConnection().use {
        it.createStatement().use { statement ->
          executeScript(filename, statement)
          commit()
        }
      }
    } catch (e: SQLException) {
      LOG.error(e.message, e)
      try {
        rollback()
      } catch (e1: SQLException) {
        LOG.error(e1.message, e1)
      }
      throw e
    }
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(ExecuteScriptOperation::class.java)

    private const val BEFORE_SCRIPT_NAME = "before.sql"
    private const val AFTER_SCRIPT_NAME = "after.sql"
  }
}