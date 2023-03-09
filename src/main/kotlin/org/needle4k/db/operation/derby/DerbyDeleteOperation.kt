package org.needle4k.db.operation.derby

import org.needle4k.db.DatabaseInjectorConfiguration
import org.needle4k.db.operation.AbstractDeleteOperation
import java.sql.SQLException
import java.sql.Statement

/**
 * Delete everything from the DB: This cannot be done with the JPA, because the
 * order of deletion matters. Instead we directly use a JDBC connection.
 */
open class DerbyDeleteOperation constructor(configuration: DatabaseInjectorConfiguration)
  : AbstractDeleteOperation(configuration) {
  @Throws(SQLException::class)
  override fun setReferentialIntegrity(enable: Boolean, statement: Statement) {
  }
}