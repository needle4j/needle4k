package org.needle4k.configuration.db.operation.derby

import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.configuration.db.operation.AbstractDeleteOperation
import java.sql.SQLException
import java.sql.Statement

/**
 * Delete everything from the DB: This cannot be done with the JPA, because the
 * order of deletion matters. Instead we directly use a JDBC connection.
 */
class DerbyDeleteOperation(needleConfiguration: NeedleConfiguration) : AbstractDeleteOperation(needleConfiguration) {
  @Throws(SQLException::class)
  override fun setReferentialIntegrity(enable: Boolean, statement: Statement) {
  }
}