package org.needle4k.db.operation.postgresql

import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.db.operation.AbstractDeleteOperation
import java.sql.SQLException
import java.sql.Statement

/**
 * Delete everything from the DB: This cannot be done with the JPA, because the
 * order of deletion matters. Instead, we directly use a JDBC connection.
 */
open class PostgresqlDeleteOperation constructor(configuration: JPAInjectorConfiguration) :
  AbstractDeleteOperation(configuration) {
  @Throws(SQLException::class)
  override fun setReferentialIntegrity(enable: Boolean, statement: Statement) {
    val tables = ArrayList<String>()

    statement.executeQuery(
      """
      SELECT table_name FROM information_schema.tables
        WHERE table_type = 'BASE TABLE'
        AND table_schema NOT IN ('pg_catalog', 'information_schema');
    """
    ).use {
      while (it.next()) {
        tables.add(it.getString(1))
      }
    }

    for (tableName in tables) {
      val sql = "ALTER TABLE " + tableName + (if (enable) " ENABLE " else " DISABLE ") + "TRIGGER ALL;"

      logger.info(sql)

      statement.execute(sql)
    }
  }
}